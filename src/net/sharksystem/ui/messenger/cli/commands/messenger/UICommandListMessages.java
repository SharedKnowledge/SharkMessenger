package net.sharksystem.ui.messenger.cli.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.ui.messenger.cli.commands.helper.AbstractCommandWithSingleInteger;
import net.sharksystem.app.messenger.SharkMessageList;
import net.sharksystem.app.messenger.SharkMessengerChannel;
import net.sharksystem.app.messenger.SharkMessengerComponent;
import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;

import java.io.IOException;

/**
 * This command lists all known messages of a channel.
 */
public class UICommandListMessages extends AbstractCommandWithSingleInteger {
    public static final int DEFAULT_CHANNEL_INDEX = 1;

    public UICommandListMessages(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                 String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand, true, DEFAULT_CHANNEL_INDEX);
    }

    @Override
    public void execute() throws Exception {
        try {
            int channelIndex = this.getIntegerArgument() - 1; // we start with 1 in UI and 0 inside
            SharkMessengerComponent messenger = this.getSharkMessengerApp().getSharkMessengerComponent();
            SharkMessengerChannel channel = null;
            try {
                channel = messenger.getChannel(channelIndex);
            }
            catch (SharkException se) {
                this.getSharkMessengerApp().tellUI("there is no channel at all (yet)");
                return;
            }

            SharkMessageList messages = channel.getMessages();
            if(messages == null || messages.size() <1) {
                this.getSharkMessengerApp().tellUI("no messages in channel " + channelIndex);
                return;
            };
            ChannelPrinter channelPrinter = new ChannelPrinter();
            this.getSharkMessengerApp().tellUI(channelPrinter.getChannelDescription(channel));

            this.getSharkMessengerApp().tellUI(
                    channelPrinter.getMessagesASString(
                            this.getSharkMessengerApp().getSharkPKIComponent(),
                            channel.getURI().toString(), messages));
        } catch (SharkException | IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        return "List messages in channel (index default: universal channel.";
    }

}
