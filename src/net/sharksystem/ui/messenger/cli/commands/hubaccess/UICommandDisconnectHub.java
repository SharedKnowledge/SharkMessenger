package net.sharksystem.ui.messenger.cli.commands.hubaccess;

import java.util.List;

import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandQuestionnaire;
import net.sharksystem.ui.messenger.cli.UICommand;

public class UICommandDisconnectHub extends UICommand {
    /**
     * Creates a command object.
     *
     * @param sharkMessengerApp
     * @param sharkMessengerUI
     * @param identifier        The identifier of the command.
     * @param rememberCommand   If the command should be saved in the history log.
     */
    public UICommandDisconnectHub(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI, String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    protected void execute() throws Exception {
        this.printTODOReimplement();
    }

    @Override
    public String getDescription() {
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
