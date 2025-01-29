package net.sharksystem.ui.messenger.cli;

import net.sharksystem.app.messenger.SharkNetMessageList;
import net.sharksystem.app.messenger.SharkNetMessagesReceivedListener;
import net.sharksystem.app.messenger.SharkNetMessengerException;

import java.io.IOException;

public class MessageReceivedListener extends SharkNetMessengerAppListener implements SharkNetMessagesReceivedListener {
    public MessageReceivedListener(SharkNetMessengerApp sharkMessengerApp) {
        super(sharkMessengerApp);
    }

    @Override
    public void sharkMessagesReceived(CharSequence uri) {
        try {
            SharkNetMessageList messages = this.sharkMessengerApp.getSharkMessengerComponent().getChannel(uri).getMessages();
            StringBuilder sb = new StringBuilder();
            sb.append(messages.size());
            sb.append("messages received in channel ");
            sb.append(uri);
            this.sharkMessengerApp.tellUI(sb.toString());

        } catch (SharkNetMessengerException | IOException e) {
            this.sharkMessengerApp.tellUIError("exception when receiving messages:" + e.getLocalizedMessage());
        }
    }
}
