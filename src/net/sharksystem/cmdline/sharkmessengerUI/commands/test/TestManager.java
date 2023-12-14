package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestManager {
    private static TestManager instance;
    private int testID;
    private String sender;

    private final Map<String, Integer> sentMessages = new HashMap<>();
    private final List<MessageData> sentMessageData = new ArrayList<>();  



    public static TestManager getInstance() {
        if (instance == null) {
            instance = new TestManager();
        }

        return instance;
    }

    private TestManager() {}

    public int sendNextMessage(String receiver, String channelUri, String content) {
        int messageCount = this.sentMessages.get(receiver);                                 // inhalt:     sender,receiver,uri,id,content (,enc,sig,txTime,rxTime,...?)
        this.sentMessages.put(receiver, ++messageCount); 
        this.sentMessageData.add(new MessageData(receiver, channelUri, content, messageCount));
        return messageCount;
    }

    public int getMessageCount(String receiver) {
        return this.sentMessages.get(receiver);
    }

    public void initNewTest(String sender, int testID) {
        this.sender = sender;
        this.testID = testID;
        this.sentMessages.clear();
    }
    private class MessageData {
        public final String receiver;
        public final String channelUri;
        public final String content;
        public final int messageID;

        public MessageData(String receiver, String channelUri, String content, int messageID) {
            this.receiver = receiver;
            this.channelUri = channelUri;
            this.content = content
            this.messageID = messageID;
        }
        
    }
}
