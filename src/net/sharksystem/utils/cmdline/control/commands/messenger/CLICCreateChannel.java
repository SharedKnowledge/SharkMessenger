package net.sharksystem.utils.cmdline.control.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICBooleanArgument;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICStringArgument;

import java.io.IOException;

public class CLICCreateChannel extends CLICommand {
    private final CLICStringArgument channelUri;
    private final CLICStringArgument channelName;
    private final CLICBooleanArgument channelMustNotExist;

    public CLICCreateChannel(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.channelUri = new CLICStringArgument();
        this.channelName = new CLICStringArgument();
        this.channelMustNotExist = new CLICBooleanArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder()
                .addQuestion("Please input the channel uri: ", this.channelUri)
                .addQuestion("Please set the channel name: ", this.channelName)
                .addQuestion("Should this channel be created, if a channel with the same uri already exists? ",
                        this.channelMustNotExist)
                .build();
    }

    @Override
    public void execute() throws Exception {
        String channelURI = this.channelUri.getValue();
        String channelName = this.channelName.getValue();

        SharkPeerFS peer = model.getPeer();

        try {
            SharkMessengerComponent peerMessenger = (SharkMessengerComponent) peer.getComponent(SharkMessengerComponent.class);
            boolean mustNotExist = this.channelMustNotExist.getValue();
            peerMessenger.createChannel(channelURI, channelName, mustNotExist);

        } catch (SharkException | IOException e) {
            ui.printError(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creates a new channel.");
        return sb.toString();
    }

}
