package net.sharksystem.utils.cmdline.control.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.utils.cmdline.control.*;

import java.io.IOException;

public class CLICRemoveChannel extends CLICommand {

    private final CLICStringArgument peerName;
    private final CLICChannelArgument channel;


    public CLICRemoveChannel(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.peerName = new CLICStringArgument();
        this.channel = new CLICChannelArgument(this.peerName);
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Peer name: ", this.peerName).
                addQuestion("Channel URI: ", this.channel).
                build();
    }

    @Override
    public void execute() throws Exception {
        try {
            SharkMessengerComponent peerMessenger = model.getMessengerFromPeer(this.peerName.getValue());
            peerMessenger.removeChannel(this.channel.getValue().getURI());

        } catch (SharkException | IOException e) {
            ui.printError(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Removes a channel.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
