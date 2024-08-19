package net.sharksystem.cmdline.sharkmessengerUI.commands.simpleMessenger;

import net.sharksystem.SharkException;
import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandIntegerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaire;
import net.sharksystem.cmdline.sharkmessengerUI.commands.extendedMessenger.ChannelPrinter;
import net.sharksystem.cmdline.sharkmessengerUI.commands.extendedMessenger.UICommandProduceChannelListBefore;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandWithSingleInteger;
import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.messenger.SharkMessengerChannel;
import net.sharksystem.messenger.SharkMessengerComponent;

import java.io.IOException;
import java.util.List;

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
            SharkMessengerChannel channel = messenger.getChannel(channelIndex);
            SharkMessageList messages = channel.getMessages();
            if(messages == null || messages.size() <1) {
                this.getSharkMessengerApp().tellUI("no messages in channel " + channelIndex);
                return;
            };
            ChannelPrinter channelPrinter = new ChannelPrinter();
            this.getSharkMessengerApp().tellUI(channelPrinter.getChannelDescription(channel));

            this.getSharkMessengerApp().tellUI(
                    channelPrinter.getMessagesASString(channel.getURI().toString(), messages));
        } catch (SharkException | IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        return "List messages in channel (index default: universal channel.";
    }

}
