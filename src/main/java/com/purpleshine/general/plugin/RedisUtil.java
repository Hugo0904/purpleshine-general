package com.purpleshine.general.plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.purpleshine.general.core.interfaces.RedisClient;
import com.purpleshine.general.core.interfaces.Singleton;
import com.purpleshine.general.core.interfaces.Switchable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;


@Singleton
public class RedisUtil implements RedisClient, Switchable {
    
    static public final RedisUtil instance;
    
    static {
        instance = new RedisUtil(); 
    }
    
    static public RedisUtil getInstance() {
        return instance;
    }
    
    private final AtomicBoolean start = new AtomicBoolean();
    private volatile JedisPool pool;
    
    private RedisUtil() {
        // singleton
    }
    
    @Override
    public boolean start(Object... options) {
        if (start.compareAndSet(false, true)) {
            switch (options.length) {
            case 3:
                pool = new JedisPool(new GenericObjectPoolConfig(), (String) options[0], (int) options[1], (int) options[2], null, Protocol.DEFAULT_DATABASE);
                
                break;
                
            case 4:
                pool = new JedisPool(new GenericObjectPoolConfig(), (String) options[0], (int) options[1], (int) options[2], (String) options[3], Protocol.DEFAULT_DATABASE);
                break;
                
            case 5:
                pool = new JedisPool(new GenericObjectPoolConfig(), (String) options[0], (int) options[1], (int) options[2], (String) options[3], (int) options[4]);
                break;

            default:
                pool = new JedisPool(new GenericObjectPoolConfig(), (String) options[0], (int) options[1], Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE);
                break;
            }
            
            System.out.println(pool.getResource().getDB());
        }
        return isRunning();
    }

    @Override
    public boolean stop() {
        if (start.compareAndSet(true, false)) {
            if (!pool.isClosed())
                pool.close();
            pool = null;
        }
        return !isRunning();
    }

    @Override
    public boolean isRunning() {
        return start.get() && !pool.isClosed();
    }
    
    @Override
    public long del(final String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.del(key);
        }
    }
    
    @Override
    public long del(final String... keys) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.del(keys);
        }
    }
    
    @Override
    public String get(final String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.get(key);
        }
    }

    @Override
    public String set(final String key, final String value) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.set(key, value);
        }
    }

    @Override
    public long hdel(final String key, final String... fields) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.hdel(key, fields);
        }
    }
    
    @Override
    public long hset(final String key, final String field, final String value) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.hset(key, field, value);
        }
    }
    
    @Override
    public String hmset(final String key, final Map<String, String> hash) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.hmset(key, hash);
        }
    }
    
    @Override
    public boolean hexists(final String key, final String field) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.hexists(key, field);
        }
    }
    
    @Override
    public String hget(final String key, final String field) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.hget(key, field);
        }
    }
    
    @Override
    public Map<String, String> hgetall(final String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.hgetAll(key);
        }
    }

    @Override
    public List<String> hmget(final String key, final String... fields) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.hmget(key, fields);
        }
    }

    @Override
    public boolean exists(final String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.exists(key);
        }
    }
    
    @Override
    public long expire(final String key, final int seconds) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.expire(key, seconds);
        }
    }
    
    @Override
    public ScanResult<String> scan(final String cursor, String match, final int count) {
        try (Jedis jedis = pool.getResource()) {
            final ScanParams scanParams = new ScanParams().count(count);
            if (match != null)
                scanParams.match(match);
            return jedis.scan(cursor, scanParams);
        }
    }
    
    @Override
    public ScanResult<String> sscan(final String key, final String cursor, final String match, final int count) {
        try (Jedis jedis = pool.getResource()) {
            final ScanParams scanParams = new ScanParams().count(count);
            if (match != null)
                scanParams.match(match);
            return jedis.sscan(key, cursor, scanParams);
        }
    }

    @Override
    public ScanResult<Entry<String, String>> hscan(final String key, final String cursor, final String match, final int count) {
        try (Jedis jedis = pool.getResource()) {
            final ScanParams scanParams = new ScanParams().count(count);
            if (match != null)
                scanParams.match(match);
            return jedis.hscan(key, cursor, scanParams);
        }
    }

    @Override
    public ScanResult<Tuple> zscan(final String key, final String cursor, final String match, final int count) {
        try (Jedis jedis = pool.getResource()) {
            final ScanParams scanParams = new ScanParams().count(count);
            if (match != null)
                scanParams.match(match);
            return jedis.zscan(key, cursor, scanParams);
        }
    }
    
    /**
     * 使用Scan查詢指定key的數量
     * @param match
     * @return
     */
    public long scanTotalKeyCount(final String match) {
        return scanTotalKey(match).size();
    }
    
    /**
     * 使用Scan查詢指定key
     * @param match
     * @return
     */
    public Collection<String> scanTotalKey(final String match) {
        final Set<String> keys = new HashSet<>();
        String cursor = ScanParams.SCAN_POINTER_START;
        do {
            final ScanResult<String> op = scan(cursor, match, 1000);
            cursor = op.getCursor();
            keys.addAll(op.getResult());
        } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        return keys;
    }
}
