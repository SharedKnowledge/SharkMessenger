package net.sharksystem.utils.cmdline.ui.commands.messenger;

import net.sharksystem.messenger.SharkMessagesReceivedListener;
import net.sharksystem.utils.cmdline.view.CLIInterface;

public class SharkMessageReceiver implements SharkMessagesReceivedListener {

    private final CLIInterface ui;

    public SharkMessageReceiver(CLIInterface ui) {
        this.ui = ui;
    }

    @Override
    public void sharkMessagesReceived(CharSequence uri) {
        //ui.displayReceivedMessage()
    }
}
