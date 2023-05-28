package net.sharksystem.utils.cmdline.ui.commands.messenger;

import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerException;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.CLICommand;

import java.io.IOException;

public abstract class CLICProduceChannelListBefore extends CLICommand {
    public CLICProduceChannelListBefore(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                           String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    protected void runBefore() {
        // produce channel list
        try {
            SharkMessengerComponent messengerComponent = this.getSharkMessengerApp().getMessengerComponent();
            ChannelPrinter.printChannelDescriptions(this.getPrintStream(), messengerComponent, true);
        } catch (IOException | SharkMessengerException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }
}
