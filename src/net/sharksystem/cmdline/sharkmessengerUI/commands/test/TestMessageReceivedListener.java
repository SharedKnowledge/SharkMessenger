package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.messenger.SharkMessagesReceivedListener;
import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.messenger.SharkMessengerException;

import java.io.IOException;

public class TestMessageReceivedListener implements SharkMessagesReceivedListener {
    private final SharkMessengerApp sharkMessengerApp;

    // as singleton
    public TestMessageReceivedListener(SharkMessengerApp sharkMessengerApp) {
        this.sharkMessengerApp = sharkMessengerApp;
    }

    @Override
    public void sharkMessagesReceived(CharSequence uri) {
        try {
            SharkMessageList messages = this.sharkMessengerApp.getMessengerComponent().getChannel(uri).getMessages();
            System.out.println(System.currentTimeMillis());
            // save received msg with timestamp
        } catch (SharkMessengerException | IOException e) {
            System.out.println("TODO: exception in MessageReceivedListener:" + e.getLocalizedMessage());
        }
    }
}
