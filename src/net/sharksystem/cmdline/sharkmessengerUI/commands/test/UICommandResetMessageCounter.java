package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import net.sharksystem.SharkException;
import net.sharksystem.cmdline.sharkmessengerUI.*;

import java.util.List;

public class UICommandResetMessageCounter extends UICommand {
    private final String sender;
    /**
     * Creates a command object.
     *
     * @param sharkMessengerApp
     * @param sharkMessengerUI
     * @param identifier        The identifier of the command.
     * @param rememberCommand   If the command should be saved in the history log.
     */
    public UICommandResetMessageCounter(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI, String identifier, boolean rememberCommand) throws SharkException {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.sender = sharkMessengerApp.getSharkPeer().getPeerID().toString();
    }

    /**
     * Put the needed parameters in a list in following order:
     * <p>
     * none
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        return true;
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }

    @Override
    protected void execute() throws Exception {
        SentMessageCounter.getInstance().resetMessageCounter();
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }
}
