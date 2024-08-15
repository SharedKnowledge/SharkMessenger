package net.sharksystem.cmdline.sharkmessengerUI.commands.extendedMessenger;

import net.sharksystem.messenger.SharkMessengerException;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandBooleanArgument;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaireBuilder;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandStringArgument;

import java.io.IOException;
import java.util.List;

/**
 * Create a channel for writing messages to it.
 */
public class UICommandCreateChannel extends UICommand {
    private final UICommandStringArgument channelUri;
    private final UICommandStringArgument channelName;
    private final UICommandBooleanArgument channelMustNotExist;

    public UICommandCreateChannel(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                  String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.channelUri = new UICommandStringArgument(sharkMessengerApp);
        this.channelName = new UICommandStringArgument(sharkMessengerApp);
        this.channelMustNotExist = new UICommandBooleanArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("Please input the channel uri: ", this.channelUri)
                .addQuestion("Please set the channel name: ", this.channelName)
                .build();
    }

    @Override
    public void execute() {
        String channelURI = this.channelUri.getValue();
        String channelName = this.channelName.getValue();

        try {
            this.getSharkMessengerApp().getSharkMessengerComponent().createChannel(channelURI, channelName, true);
        } catch (SharkMessengerException | IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creates a new channel.");
        return sb.toString();
    }
    
    /**
     * @param arguments in following order:
     * <ol>
     *  <li>channelUri - String</li>
     *  <li>channelName - String</li>
     *  <li>channelMustNotExist - boolean</li>
     * </ol>
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if(arguments.size() < 3) {
            return false;
        }
        boolean isParsable = channelUri.tryParse(arguments.get(0))
                && channelName.tryParse(arguments.get(1))
                && channelMustNotExist.tryParse(arguments.get(2));

        return isParsable;
    }

}
