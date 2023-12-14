package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentMessageCounter {
    private static SentMessageCounter instance;


    private final Map<String, Integer> sentMessages = new HashMap<>();
    private final List<MessageData> sentMessageData = new ArrayList<>();  



    public static SentMessageCounter getInstance() {
        if (instance == null) {
            instance = new SentMessageCounter();
        }

        return instance;
    }

    private SentMessageCounter() {}

    public int sendNextMessage(String receiver, String channelUri, String content) {
        if (!sentMessages.containsKey(receiver)) {
            sentMessages.put(receiver,-1);
        }
        int messageCount = this.sentMessages.get(receiver);                                 // inhalt:     sender,receiver,uri,id,content (,enc,sig,txTime,rxTime,...?)
        this.sentMessages.put(receiver, ++messageCount); 
        this.sentMessageData.add(new MessageData(receiver, channelUri, content, messageCount));
        return messageCount;
    }

    public int getMessageCount(String receiver) {
        return this.sentMessages.get(receiver);
    }

    public void resetMessageCounter() {
        this.sentMessages.clear();
        this.sentMessageData.clear();
    }

    public List<String> getMessageData() {
        List<String> messageData = new ArrayList<>();
        for (MessageData data : sentMessageData) {
            StringBuilder sb = new StringBuilder();
            sb.append(data.receiver);
            sb.append(",");
            sb.append(data.channelUri);
            sb.append(",");
            sb.append(data.messageID);
            messageData.add(sb.toString());
        }
        return messageData;
    }

    private class MessageData {
        public final String receiver;
        public final String channelUri;
        public final String content;
        public final int messageID;

        public MessageData(String receiver, String channelUri, String content, int messageID) {
            this.receiver = receiver;
            this.channelUri = channelUri;
            this.content = content;
            this.messageID = messageID;
        }
        
    }
}
