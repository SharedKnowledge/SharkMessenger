package net.sharksystem.cmdline.sharkmessengerUI.commands.messenger;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
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
            SharkMessengerComponent messengerComponent = this.getSharkMessengerApp().getMessengerComponent();
            ChannelPrinter.printChannelDescriptions(this.getPrintStream(), messengerComponent, true);
        } catch (IOException | SharkMessengerException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }
}
