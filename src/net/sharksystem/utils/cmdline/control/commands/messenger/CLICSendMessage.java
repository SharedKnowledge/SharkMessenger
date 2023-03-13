package net.sharksystem.utils.cmdline.control.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.utils.cmdline.control.*;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CLICSendMessage extends CLICommand {

    private final CLICSharkPeerArgument peer;
    private final CLICChannelArgument channel;
    private final CLICBooleanArgument sign;
    private final CLICBooleanArgument encrypt;
    private final CLICStringArgument message;
    private final CLICStringArgument receivers;


    public CLICSendMessage(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.peer = new CLICSharkPeerArgument();
        this.channel = new CLICChannelArgument(this.peer);
        this.sign = new CLICBooleanArgument();
        this.encrypt = new CLICBooleanArgument();
        this.message = new CLICStringArgument();
        this.receivers = new CLICStringArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Sender peer name: ", this.peer).
                addQuestion("Channel URI: ", this.channel).
                addQuestion("Sign? ", this.sign).
                addQuestion("Encrypt? ", this.encrypt).
                addQuestion("Message: ", this.message).
                addQuestion("Receivers: ", this.receivers).
                build();
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model) throws Exception {
        try {
            SharkMessengerComponent messenger = (SharkMessengerComponent) this.peer.getValue().
                    getComponent(SharkMessengerComponent.class);

            CharSequence channelURI = this.channel.getValue().getURI();
            boolean sing = this.sign.getValue();
            boolean encrypt = this.encrypt.getValue();
            byte[] message = this.message.getValue().getBytes();

            Set<CharSequence> receivers = this.getAllExistingPeers(this.receivers.getValue(), model);
            messenger.sendSharkMessage(message, channelURI, receivers, sing, encrypt);

        } catch (SharkException | IOException e) {
            ui.printError(e.getLocalizedMessage());
        }
    }

    private Set<CharSequence> getAllExistingPeers(String s, CLIModelInterface model) {
        Set<CharSequence> peers = new HashSet<>();
        String[] stringPeers = s.split(",");
        for (String peerName : stringPeers) {
            if (model.hasPeer(peerName)) {
                peers.add(peerName);
            }
        }
        return peers.size() > 0 ? peers : null;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sends a message into a channel.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
