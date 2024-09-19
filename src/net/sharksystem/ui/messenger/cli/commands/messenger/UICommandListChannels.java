package net.sharksystem.ui.messenger.cli.commands.messenger;

import net.sharksystem.app.messenger.SharkMessengerComponent;
import net.sharksystem.app.messenger.SharkMessengerException;
import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandQuestionnaire;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandQuestionnaireBuilder;
import net.sharksystem.ui.messenger.cli.UICommand;

import java.io.IOException;
import java.util.List;

/**
 * This command lists all known channels for the peer.
 */
public class UICommandListChannels extends UICommand {
    /**
     * Creates a command object.
     *
     * @param sharkMessengerApp
     * @param sharkMessengerUI
     * @param identifier        The identifier of the command.
     * @param rememberCommand   If the command should be saved in the history log.
     */
    public UICommandListChannels(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                 String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder().build();
    }

    @Override
    protected void execute() throws Exception {
        try {
            SharkMessengerComponent messengerComponent = this.getSharkMessengerApp().getSharkMessengerComponent();

            new ChannelPrinter().getChannelDescriptions(messengerComponent);
        } catch (SharkMessengerException | IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        return "List existing channels.";
    }
    
    /**
     * This command requires no arguments.
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
       return true;
    }
}
