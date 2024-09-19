package net.sharksystem.ui.messenger.cli.commands.helper;

import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;
import net.sharksystem.ui.messenger.cli.UICommand;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandQuestionnaire;

import java.util.List;

/**
 * Command for terminating the messenger.
 */
public abstract class AbstractCommandNoParameter extends UICommand {
    public AbstractCommandNoParameter(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                      String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return null;
    }

    /**
     * This command requires no arguments.
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        return true;
    }

}
