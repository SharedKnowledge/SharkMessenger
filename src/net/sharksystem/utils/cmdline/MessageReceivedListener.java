package net.sharksystem.utils.cmdline;

import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.messenger.SharkMessagesReceivedListener;
import net.sharksystem.messenger.SharkMessengerException;
import net.sharksystem.utils.cmdline.model.CLIModel;

import java.io.IOException;

public class MessageReceivedListener extends SharkMessengerAppListener implements SharkMessagesReceivedListener {
    public MessageReceivedListener(SharkMessengerApp sharkMessengerApp) {
        super(sharkMessengerApp);
    }

    @Override
    public void sharkMessagesReceived(CharSequence uri) {
        try {
            SharkMessageList messages = this.sharkMessengerApp.getMessengerComponent().getChannel(uri).getMessages();
            System.out.println("TODO in MessageReceivedListener: show messages");
            //cliModel.observer.displayMessages(messages);

        } catch (SharkMessengerException | IOException e) {
            System.out.println("TODO: exception in MessageReceivedListener:" + e.getLocalizedMessage());

//            cliModel.observer.onChannelDisappeared(uri.toString());
        }
    }
}
