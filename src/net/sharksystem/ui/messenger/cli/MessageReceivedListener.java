package net.sharksystem.ui.messenger.cli;

import net.sharksystem.app.messenger.SharkMessageList;
import net.sharksystem.app.messenger.SharkMessagesReceivedListener;
import net.sharksystem.app.messenger.SharkMessengerException;

import java.io.IOException;

public class MessageReceivedListener extends SharkMessengerAppListener implements SharkMessagesReceivedListener {
    public MessageReceivedListener(SharkMessengerApp sharkMessengerApp) {
        super(sharkMessengerApp);
    }

    @Override
    public void sharkMessagesReceived(CharSequence uri) {
        try {
            SharkMessageList messages = this.sharkMessengerApp.getSharkMessengerComponent().getChannel(uri).getMessages();
            StringBuilder sb = new StringBuilder();
            sb.append(messages.size());
            sb.append("messages received in channel ");
            sb.append(uri);
            this.sharkMessengerApp.tellUI(sb.toString());

        } catch (SharkMessengerException | IOException e) {
            this.sharkMessengerApp.tellUIError("exception when receiving messages:" + e.getLocalizedMessage());
        }
    }
}
