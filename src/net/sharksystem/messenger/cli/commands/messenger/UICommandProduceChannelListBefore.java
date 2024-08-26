package net.sharksystem.messenger.cli.commands.messenger;

import net.sharksystem.messenger.cli.SharkMessengerApp;
import net.sharksystem.messenger.cli.SharkMessengerUI;
import net.sharksystem.messenger.cli.UICommand;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerException;

import java.io.IOException;

public abstract class UICommandProduceChannelListBefore extends UICommand {
    public UICommandProduceChannelListBefore(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                             String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    protected void runBefore() {
        // produce channel list
        try {
            SharkMessengerComponent messengerComponent = this.getSharkMessengerApp().getSharkMessengerComponent();
            new ChannelPrinter().getChannelDescriptions(messengerComponent);
        } catch (IOException | SharkMessengerException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }
}
