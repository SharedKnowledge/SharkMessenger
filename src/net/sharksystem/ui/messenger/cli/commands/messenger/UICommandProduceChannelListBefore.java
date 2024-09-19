package net.sharksystem.ui.messenger.cli.commands.messenger;

import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;
import net.sharksystem.ui.messenger.cli.UICommand;
import net.sharksystem.app.messenger.SharkMessengerComponent;
import net.sharksystem.app.messenger.SharkMessengerException;

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
