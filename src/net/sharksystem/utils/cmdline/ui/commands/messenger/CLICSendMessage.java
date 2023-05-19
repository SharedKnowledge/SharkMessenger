package net.sharksystem.utils.cmdline.ui.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.*;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CLICSendMessage extends CLICommand {
    private final CLICChannelArgument channel;
    private final CLICBooleanArgument sign;
    private final CLICBooleanArgument encrypt;
    private final CLICStringArgument message;
    private final CLICStringArgument receivers;


    public CLICSendMessage(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                           String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.channel = new CLICChannelArgument();
        this.sign = new CLICBooleanArgument();
        this.encrypt = new CLICBooleanArgument();
        this.message = new CLICStringArgument();
        this.receivers = new CLICStringArgument();
        this.receivers.setEmptyStringAllowed(true);
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder()
                .addQuestion("Channel URI: ", this.channel)
                .addQuestion("Sign? ", this.sign)
                .addQuestion("Encrypt? ", this.encrypt)
                .addQuestion("Message: ", this.message)
                .addQuestion("Receivers (leave blank for anybody): ", this.receivers)
                .build();
    }

    @Override
    public void execute() throws Exception {
        try {
            SharkMessengerComponent messenger = this.getSharkMessengerApp().getMessengerComponent();

            CharSequence channelURI = this.channel.getValue().getURI();
            boolean sign = this.sign.getValue();
            boolean encrypt = this.encrypt.getValue();
            byte[] message = this.message.getValue().getBytes();

            Set<CharSequence> receivers = this.getAllExistingPeers(this.receivers.getValue(), model);
            messenger.sendSharkMessage(message, channelURI, receivers, sign, encrypt);

        } catch (SharkException | IOException e) {
            ui.printError(e.getLocalizedMessage());
        }
    }

    private Set<CharSequence> getAllExistingPeers(String s, CLIModelInterface model) {
        Set<CharSequence> peers = new HashSet<>();
        String[] stringPeers = s.split(",");
        for (String peerName : stringPeers) {
            //TODO: Don't know how to save other peers yet
        }
        return peers.size() > 0 ? peers : null;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sends a message into a channel.");
        return sb.toString();
    }

}
