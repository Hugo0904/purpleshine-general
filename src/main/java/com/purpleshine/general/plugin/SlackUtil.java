package com.purpleshine.general.plugin;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.SlackPreparedMessage;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.replies.SlackMessageReply;

public final class SlackUtil {
    static SlackUtil instance;
    
    static {
        instance = new SlackUtil();
     } 

    static public SlackUtil getInstance() {
        return instance;
    }
    
    private final ConcurrentMap<String, Slack> slackSessions = new ConcurrentHashMap<>();
    
    private SlackUtil() {
        // singleton
    }

    /**
     * 連線至指定Slack
     * @param slackName slack名稱
     * @param slackBotAuthToken
     * @return
     * @throws IOException
     */
    public synchronized Slack connect(final String slackName, final String slackBotAuthToken) throws IOException {
        Slack manager = slackSessions.get(slackName);
        if (Objects.isNull(manager) || manager.isConnected() == false) {
            final SlackSession session = SlackSessionFactory.createWebSocketSlackSession(slackBotAuthToken);
            session.connect();
            manager = new Slack(session);
            slackSessions.put(slackName, manager);
            return manager;
        }
        return slackSessions.get(slackName);
    }
    
    /**
     * 關閉Slack
     * @param slackName
     * @return
     * @throws IOException
     */
    public synchronized SlackUtil disconnect(final String slackName) throws IOException {
        final Slack manager = slackSessions.remove(slackName);
        if (Objects.nonNull(manager) && manager.isConnected()) {
            manager.close();
        }
        return this;
    }
    
    /**
     * 關閉所有Slack
     * @return
     * @throws IOException
     */
    public synchronized SlackUtil shutdownNow() {
        for (String slackName : slackSessions.keySet()) {
            try {
                final Slack manager = slackSessions.remove(slackName);
                manager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
    
    /**
     * 取得當前所有已連線Slack名稱
     * @return
     */
    public Collection<String> getAllSlackName() {
        return slackSessions.keySet();
    }
    
    /**
     * 發送訊息至Slack
     * @param slackName
     * @param channel
     * @param message
     * @return
     * @throws Exception
     */
    public Slack sendMessage(final String slackName, final String channel, final String message) throws Exception {
        if (!slackSessions.containsKey(slackName)) throw new Exception("not found slack " + slackName);
        final Slack manager = slackSessions.get(slackName);
        manager.sendMessage(channel, message);
        return manager;
    }
    
    /**
     * 發送有附加內容的訊息至Slack
     * @param slackName
     * @param channel
     * @param message
     * @param slackAttachment
     * @return
     * @throws Exception
     */
    public Slack sendMessage(final String slackName, final String channel, final String message, final SlackAttachment slackAttachment) throws Exception {
        if (!slackSessions.containsKey(slackName)) throw new Exception("not found slack " + slackName);
        final Slack manager = slackSessions.get(slackName);
        manager.sendMessage(channel, message, slackAttachment);
        return manager;
    }
    
    /**
     * 發送訊息至所有已創建的slack
     * @param channel
     * @param message
     * @return
     * @throws Exception
     */
    public SlackUtil sendMessageToAllSlack(final String channel, final String message) throws Exception {
        for (Slack manager : slackSessions.values()) {
            manager.sendMessage(channel, message);
        }
        return this;
    }
    
    /**
     * 發送有附加內容的訊息至所有已創建的slack
     * @param channel
     * @param message
     * @param slackAttachment
     * @return
     * @throws Exception
     */
    public SlackUtil sendMessageToAllSlack(final String channel, final String message, final SlackAttachment slackAttachment) throws Exception {
        for (Slack manager : slackSessions.values()) {
            manager.sendMessage(channel, message, slackAttachment);
        }
        return this;
    }
    
    static final public class Slack {
        
        final SlackSession session;

        private Slack(SlackSession session) {
            this.session = session;
        }

        public boolean isConnected() {
            return session.isConnected();
        }
        
        public void close() throws IOException {
            session.disconnect();
        }
        
        public void sendMessage(final String channel, final String message) throws Exception {
            final SlackChannel _channel = session.findChannelByName(channel);
            session.sendMessage(_channel, message);
        }
        
        public SlackMessageHandle<SlackMessageReply> sendMessage(final String channel, final String message, final SlackAttachment slackAttachment) throws Exception {
            final SlackChannel _channel = session.findChannelByName(channel);
            SlackPreparedMessage preparedMessage = new SlackPreparedMessage.Builder()
                    .withMessage(message)
                    .withUnfurl(false)
                    .addAttachment(slackAttachment)
                    .build();
            return session.sendMessage(_channel, preparedMessage);
        }

        public SlackSession getSession() {
            return session;
        }
        
    }
}