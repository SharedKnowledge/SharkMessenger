package net.sharksystem.cmdline.sharkmessengerUI.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.messenger.SharkMessengerChannel;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UICommandSendMessage extends UICommandProduceChannelListBefore {
    private final UICommandIntegerArgument channelIndex;
    private final UICommandBooleanArgument sign;
    private final UICommandBooleanArgument encrypt;
    private final UICommandStringArgument message;
    private final UICommandStringArgument receivers;


    public UICommandSendMessage(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.channelIndex = new UICommandIntegerArgument(sharkMessengerApp);
        this.sign = new UICommandBooleanArgument(sharkMessengerApp);
        this.encrypt = new UICommandBooleanArgument(sharkMessengerApp);
        this.message = new UICommandStringArgument(sharkMessengerApp);
        this.receivers = new UICommandStringArgument(sharkMessengerApp);
        this.receivers.setEmptyStringAllowed(true);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("Index target channel (0..n): ", this.channelIndex)
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

            int channelIndex = this.channelIndex.getValue();
            SharkMessengerChannel channel = messenger.getChannel(channelIndex);
            boolean sign = this.sign.getValue();
            boolean encrypt = this.encrypt.getValue();
            byte[] message = this.message.getValue().getBytes();

            Set<CharSequence> receivers = this.getAllExistingPeers(this.receivers.getValue());
            messenger.sendSharkMessage(message, channel.getURI(), receivers, sign, encrypt);
        } catch (SharkException | IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    private Set<CharSequence> getAllExistingPeers(String s) {
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
