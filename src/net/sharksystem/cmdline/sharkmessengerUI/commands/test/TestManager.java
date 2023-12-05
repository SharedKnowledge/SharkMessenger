package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

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

    private final Map<String, Integer> sentMessages = new HashMap<>();

    private TestManager() {}

    public int sendNextMessage(String username) {
        int messageCount = this.sentMessages.get(username);
        this.sentMessages.put(username, ++messageCount);
        return messageCount;
    }

    public int getMessageCount(String username) {
        return this.sentMessages.get(username);
    }

    public void resetManager() {
        if (instance == null) {
            return;
        }

        this.sentMessages.clear();
    }
}
