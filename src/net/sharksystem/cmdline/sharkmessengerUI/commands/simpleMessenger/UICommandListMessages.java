package net.sharksystem.cmdline.sharkmessengerUI.commands.simpleMessenger;

import net.sharksystem.SharkException;
import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.cmdline.sharkmessengerUI.commands.extendedMessenger.UICommandProduceChannelListBefore;
import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.messenger.SharkMessengerChannel;
import net.sharksystem.messenger.SharkMessengerComponent;

import java.io.IOException;
import java.util.List;

/**
 * This command lists all known messages of a channel.
 */
public class UICommandListMessages extends UICommandProduceChannelListBefore {
    public UICommandListMessages(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                 String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        /*
        return new UICommandQuestionnaireBuilder()
                .addQuestion("Index target channel (0..n): ", this.channelIndex)
                .build();
         */
        return null;
    }

    @Override
    public void execute() throws Exception {
        try {
            SharkMessengerChannel universeChannel = this.getSharkMessengerApp().getSharkMessengerComponent().
                    getChannel(SharkMessengerComponent.UNIVERSAL_CHANNEL_URI);
            SharkMessageList messages = universeChannel.getMessages();
            if(messages == null || messages.size() <1) {
                this.getSharkMessengerApp().tellUI("no messages in universe channel");
                return;
            };
            new ChannelPrinter(this.getSharkMessengerApp()).
                    printMessages(SharkMessengerComponent.UNIVERSAL_CHANNEL_URI, this.getPrintStream(), messages);
        } catch (SharkException | IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        return "List messages in universe channel.";
    }

    /**
     * @param arguments in following order:
     * <ol>
     *  <li>channelIndex - int</li>
     * </ol>
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        /*
        if(arguments.size() < 1) {
            return false;
        }
        
        boolean isParsable = channelIndex.tryParse(arguments.get(0));
        return isParsable;
         */
        return true;
    }
}
