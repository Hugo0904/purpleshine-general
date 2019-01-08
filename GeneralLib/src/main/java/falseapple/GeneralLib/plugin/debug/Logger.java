package falseapple.GeneralLib.plugin.debug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import falseapple.GeneralLib.core.ThreadPool;
import falseapple.GeneralLib.core.interfaces.Switchable;
import falseapple.GeneralLib.helpers.IOUtil;
import falseapple.GeneralLib.plugin.PublishScheduler;
import lombok.Builder;
import lombok.Data;

/**
 * @author yueh
 * 重新封裝apache logger
 * 管理logger和事件觸發
 */
public final class Logger extends PublishScheduler<Void> implements Switchable {
    
    /**
     * 
     * @author yueh
     * 日誌等級
     */
    static public enum LogLevel {
        DEBUG(0), INFO(1), WARN(2), ERROR(3), FATAL(4);
        private final int level;
        private LogLevel(int level) {
            this.level = level;
        }
        public int getLevel() {
            return level;
        }
    }
    
    static public LogRecorder defaultRecorder = new LogRecorder("Default");
    static private final Logger instance;
    
    static {
        instance = new Logger();
    }
    
    static public final Logger getInstance() {
        return instance;
    }
    
    static public int cleanLogs(Path path, Instant before) {
        try {
            final AtomicInteger deleteCount = new AtomicInteger();
            Files.walk(path)
                .filter(Files::isRegularFile)
                .filter(f -> isFileBefore(f, before))
                .map(Path::toFile)
                .collect(Collectors.toList())
                .forEach(f -> {
                    if (f.delete()) {
                        deleteCount.incrementAndGet();
                        System.out.println("移除" + f.getName());
                    }
                });
            return deleteCount.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    static private boolean isFileBefore(final Path path, final Instant before) {
        try {
            return Files.getLastModifiedTime(path).toInstant().isBefore(before);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private final int clearPeriod = 6; // 週期6小時 (秒)

    private final AtomicBoolean run = new AtomicBoolean();
    private final ConcurrentMap<LogRecorder, org.apache.logging.log4j.Logger> loggers = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<Consumer<LogEvent>> events = new CopyOnWriteArrayList<>();

    private volatile LogConfig config;
    private Instant lastClear = Instant.ofEpochMilli(0);
    private LogLevel consoleOutput = LogLevel.WARN;
    
    private Logger() {
        //
    }
    
    /**
     * 第一個參數需放入 LogConfig
     */
    @Override
    public boolean start(Object... options) {
        final LogConfig config = (LogConfig) options[0];
        if (this.run.compareAndSet(false, true)) {
            this.config = config;
            final Path path = Paths.get(Objects.requireNonNull(config.getLogDirPath()));
            if (Files.notExists(path)){
                try {
                    Files.createDirectory(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            executetWithFixedSchedule(this, () -> {
                if (Files.exists(path)) {
                    final Instant before = LocalDateTime.now().plusDays(-Math.abs(config.getKeepDay())).toInstant(ZoneOffset.UTC);
                    final int cleanSize = cleanLogs(path, before);
                    System.out.println("總共清除了 " + cleanSize + " 個歷史記錄檔案...");
                }
                lastClear = Instant.now();
            }, 0, clearPeriod, TimeUnit.HOURS);
        }
        return this.run.get();
    }

    @Override
    public boolean stop() {
        if (this.run.compareAndSet(true, false)) {
            cancelScheduleByName(this, true);
            config = null;
        }
        return !this.run.get();
    }

    @Override
    public boolean isRunning() {
        return this.run.get() && Duration.between(lastClear, Instant.now()).toMillis() <= clearPeriod + 60 * 1000;
    }
    
    /**
     * Debug
     * @param logger
     * @param msg
     * @param args
     * @return
     */
    static public String debug(LogRecorder logger, int stamp, String msg, Object... args) {
        return instance.Log(logger, LogLevel.DEBUG, stamp, msg, args);
    }

    /**
     * Info
     * @param logger
     * @param msg
     * @param args
     * @return
     */
    static public String info(LogRecorder logger, int stamp, String msg, Object... args) {
        return instance.Log(logger, LogLevel.INFO, stamp, msg, args);
    }

    /**
     * Warn
     * @param logger
     * @param msg
     * @param args
     * @return
     */
    static public String warn(LogRecorder logger, int stamp, String msg, Object... args) {
        return instance.Log(logger, LogLevel.WARN, stamp, msg, args);
    }

    /**
     * Error
     * @param logger
     * @param msg
     * @param args
     * @return
     */
    static public String error(LogRecorder logger, int stamp, String msg, Object... args) {
        return instance.Log(logger, LogLevel.ERROR, stamp, msg, args);
    }

    /**
     * fatal
     * @param logger
     * @param msg
     * @param args
     * @return
     */
    static public String fatal(LogRecorder logger, int stamp, String msg, Object... args) {
        return instance.Log(logger, LogLevel.FATAL, stamp, msg, args);
    }
    
    /**
     * 
     * @param recorder
     * @param level
     * @param message
     * @param args
     * @return
     */
    public String Log(LogRecorder recorder, LogLevel level, int stamp, String message, final Object... args) {
        try {
            if (Objects.nonNull(args) && args.length > 0)
                message = String.format(message, args);
            
//            message = String.format("[%d]\n%s", Thread.currentThread().getId(), message);
            
            if (Objects.nonNull(config)) {
                org.apache.logging.log4j.Logger logger;
                if (!loggers.containsKey(recorder)) {
                    logger = config.getFunction().apply(recorder);
                    if (Objects.isNull(logger)) {
                        logger = getDefaultLogger();
                    } else
                        loggers.putIfAbsent(recorder, logger);
                } else {
                    logger = loggers.get(recorder);
                }
                
                switch (level) {
                    case DEBUG:
                       logger.debug(message);
                        break;
                    case WARN:
                       logger.warn(message);
                        break;
                    case INFO:
                       logger.info(message);
                        break;
                    case FATAL:
                       logger.fatal(message);
                        break;
                    case ERROR:
                       logger.error(message);
                        break;
                }
            }
            
            if (isLevelFully(level)) {
                executeEvent(stamp, level, message);
                System.out.println(message);
            }
    
        } catch (Exception e){
            System.out.println("Set log fail : " + recorder + " = " + level + " => " + message + ", Exception: " + IOUtil.traceToString(e));
        }
        return message;
    }
    
    private org.apache.logging.log4j.Logger getDefaultLogger() {
        loggers.putIfAbsent(defaultRecorder, config.getFunction().apply(defaultRecorder));
        return loggers.get(defaultRecorder);
    }
    
    private boolean isLevelFully(LogLevel level) {
        return level.getLevel() >= consoleOutput.getLevel();
    }
    
    private void executeEvent(int stamp, LogLevel level, String message) {
        events.stream()
            .collect(Collectors.toList())
            .forEach(event -> ThreadPool.execute(() -> event.accept(new LogEvent(level, message, stamp))));
    }
    
    public void addEvent(Consumer<LogEvent> consumer) {
        events.add(consumer);
    }
    
    public void removeEvent(Consumer<LogEvent> consumer, LogLevel levelType) {
        events.remove(consumer);
    }
    
    public LogLevel getConsoleOutput() {
        return consoleOutput;
    }

    public void setConsoleOutput(LogLevel consoleOutput) {
        this.consoleOutput = consoleOutput;
    }
    
    /**
     * 
     * @author yueh
     * 日誌配置
     */
    @Data
    @Builder
    static public final class LogConfig {
        
        private Function<LogRecorder, org.apache.logging.log4j.Logger> function;
        private String logDirPath; // 儲存Log的路徑
        private int keepDay; // 保留記錄天數
        
        public Function<LogRecorder, org.apache.logging.log4j.Logger> getFunction() {
            return function;
        }
        public void setFunction(Function<LogRecorder, org.apache.logging.log4j.Logger> function) {
            this.function = function;
        }
        public String getLogDirPath() {
            return logDirPath;
        }
        public void setLogDirPath(String logDirPath) {
            this.logDirPath = logDirPath;
        }
        public int getKeepDay() {
            return keepDay;
        }
        public void setKeepDay(int keepDay) {
            this.keepDay = keepDay;
        }

    }
    
    /**
     * @author yueh
     * 取得記錄者的名稱
     */
    static final public class LogRecorder {
        
        private final String recorderName;

        public LogRecorder(String recorderName) {
            this.recorderName = recorderName;
        }

        public String getRecorderName() {
            return recorderName;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((recorderName == null) ? 0 : recorderName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            
            if (obj == null || getClass() != obj.getClass())
                return false;
            
            final LogRecorder other = (LogRecorder) obj;
            if (recorderName == other.recorderName) {
                return true;
            } else {
                return recorderName.equals(other.recorderName);             
            }
        }
        
        @Override
        public String toString() {
            return this.recorderName;
        }
    }
    
    static public final class LogEvent {
        
        private final LogLevel level;
        private final String message;
        private final int stamp;
        
        public LogEvent(LogLevel level, String message, int stamp) {
            this.level = level;
            this.message = message;
            this.stamp = stamp;
        }

        public LogLevel getLevel() {
            return level;
        }

        public String getMessage() {
            return message;
        }

        public int getStamp() {
            return stamp;
        }
    }
}