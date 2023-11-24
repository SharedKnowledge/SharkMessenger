package net.sharksystem.cmdline.sharkmessengerUI;

import java.util.HashMap;
import java.util.Map;

public class TestManager {
    private static TestManager instance;

    public static TestManager getInstance() {
        if (instance == null) {
            instance = new TestManager();
        }

        return instance;
    }

    private Map<String, Integer> sentMessages = new HashMap<>();

    private TestManager() {}

    public int sendNextMessage(String username) {
        int messageCount = this.sentMessages.get(username);
        this.sentMessages.put(username, ++messageCount);
        return messageCount;
    }

    public int getMessageCount(String username) {
        return this.sentMessages.get(username);
    }
}
