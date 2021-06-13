package net.sharksystem.messenger;

public class TestHelper {
    private static int portNumber = 5000;

    public static int getPortNumber() {
        return TestHelper.portNumber++;
    }
}
