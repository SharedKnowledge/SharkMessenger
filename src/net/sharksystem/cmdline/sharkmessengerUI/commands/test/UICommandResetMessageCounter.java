package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import net.sharksystem.SharkException;
import net.sharksystem.cmdline.sharkmessengerUI.*;

import java.util.List;

/**
 * This command resets all IDs and information about sent messages to reset the state after saving the results to a file
 * and before the next test run.
 */
public class UICommandResetMessageCounter extends UICommand {
    private final String sender;
    private final String peerName;

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
        this.peerName = sharkMessengerApp.getSharkPeer().getPeerID().toString();
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
    protected void execute() throws Exception {
        SentMessageCounter.getInstance(this.peerName).resetMessageCounter();
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }

    @Override
    public String getDescription() {
        return "Resets all saved information about sent messages in SentMessageCounter. Use between test runs.";
    }
}
