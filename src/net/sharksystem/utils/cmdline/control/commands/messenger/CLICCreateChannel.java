package net.sharksystem.utils.cmdline.control.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICBooleanArgument;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICStringArgument;

import java.io.IOException;

public class CLICCreateChannel extends CLICommand {
    private final CLICStringArgument peerName;
    private final CLICStringArgument channelUri;
    private final CLICStringArgument channelName;
    private final CLICBooleanArgument channelMustNotExist;

    public CLICCreateChannel(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.peerName = new CLICStringArgument();
        this.channelUri = new CLICStringArgument();
        this.channelName = new CLICStringArgument();
        this.channelMustNotExist = new CLICBooleanArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Please insert the peer name: ", this.peerName).
                addQuestion("Please input the channel uri: ", this.channelUri).
                addQuestion("Please set the channel name: ", this.channelName).
                addQuestion("Should this channel be created, if a channel with the same uri already exists? ",
                        this.channelMustNotExist).
                build();
    }

    @Override
    public void execute() throws Exception {
        String peerName = this.peerName.getValue();
        String channelURI = this.channelUri.getValue();
        String channelName = this.channelName.getValue();

        if (model.hasPeer(peerName)) {
            SharkTestPeerFS peer = model.getPeer(peerName);

            try {
                SharkMessengerComponent peerMessenger = (SharkMessengerComponent) peer.getComponent(SharkMessengerComponent.class);
                boolean mustNotExist = this.channelMustNotExist.getValue();
                peerMessenger.createChannel(channelURI, channelName, mustNotExist);

            } catch (SharkException | IOException e) {
                ui.printError(e.getLocalizedMessage());
            }
        } else {
            ui.printError("Mentioned peer doesn't exists! Create peer first.");
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creates a new channel.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
