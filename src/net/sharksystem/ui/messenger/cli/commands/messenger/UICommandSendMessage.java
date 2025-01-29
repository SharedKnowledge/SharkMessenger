package net.sharksystem.ui.messenger.cli.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.app.messenger.SharkNetMessage;
import net.sharksystem.asap.utils.ASAPSerialization;
import net.sharksystem.app.messenger.SharkNetMessengerChannel;
import net.sharksystem.app.messenger.SharkNetMessengerComponent;
import net.sharksystem.ui.messenger.cli.SharkNetMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkNetMessengerUI;
import net.sharksystem.ui.messenger.cli.commandarguments.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This command sends a message into a channel.
 */
public class UICommandSendMessage extends UICommandProduceChannelListBefore {
    private final UICommandIntegerArgument channelIndexArgument;
    private final UICommandBooleanArgument signArgument;
    private final UICommandStringArgument messageArgument;

    private String message;
    private int channelIndex;
    private boolean sign;

    public UICommandSendMessage(SharkNetMessengerApp sharkMessengerApp, SharkNetMessengerUI sharkMessengerUI,
                                String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.messageArgument = new UICommandStringArgument(sharkMessengerApp);
        this.channelIndexArgument = new UICommandIntegerArgument(sharkMessengerApp);
        this.signArgument = new UICommandBooleanArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("Index target channel (0..n): ", this.channelIndexArgument)
                .addQuestion("Sign? ", this.signArgument)
                //.addQuestion("Encrypt? ", this.encrypt)
                .addQuestion("Message: ", this.messageArgument)
                //.addQuestion("Receivers (leave blank for anybody): ", this.receivers)
                .build();
    }

    @Override
    public void execute() throws Exception {
        try {
            SharkNetMessengerComponent messenger = this.getSharkMessengerApp().getSharkMessengerComponent();
            int index = this.channelIndex - 1;
            SharkNetMessengerChannel channel;
            CharSequence channelURI;
            try {
                channelURI = messenger.getChannel(index).getURI();
            }
            catch(SharkException se) {
                if(this.channelIndex == 1) {
                    // okay, go with default
                    channelURI = SharkNetMessengerComponent.UNIVERSAL_CHANNEL_URI;
                } else {
                    this.getSharkMessengerApp().tellUIError("create a channel on index " + this.channelIndex + "before.");
                    return;
                }
            }

            // serialize message
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ASAPSerialization.writeCharSequenceParameter(this.message, baos);
            byte[] messageContent = baos.toByteArray();
            messenger.sendSharkMessage(SharkNetMessage.SN_CONTENT_TYPE_ASAP_CHARACTER_SEQUENCE,
                    messageContent, channelURI, this.sign);
        } catch (SharkException | IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    private Set<CharSequence> getAllExistingPeers(String s) {
        Set<CharSequence> peers = new HashSet<>();
        String[] stringPeers = s.split(",");
        for (String peerName : stringPeers) {
            peers.add(peerName);
        }
        return peers.size() > 0 ? peers : null;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sends a message into a channel (required: message (no spaces allowed - it is just an example app); " +
                "optional: channel index | sign (true/false");
        return sb.toString();
    }

    @Override
    protected boolean handleArguments(List<String> arguments) {
        if(arguments.size() < 1) {
            this.getSharkMessengerApp().tellUIError("required: message content (a string, no spaces allowed)");
            return false;
        }

        if(!messageArgument.tryParse(arguments.get(0))) {
            this.getSharkMessengerApp().tellUIError("cannot parse message: " + arguments.get(0));
            return false;
        } else {
            this.message = this.messageArgument.getValue();
        }

        this.channelIndex = 1; // set default
        if(arguments.size() > 1) {
            if(channelIndexArgument.tryParse(arguments.get(1))) {
                // overwrite default
                this.channelIndex = this.channelIndexArgument.getValue();
            }
        }

        this.sign = false; // set default
        if(arguments.size() > 2) {
            // overwrite default
            if(signArgument.tryParse(arguments.get(2))) {
                this.sign = this.signArgument.getValue();
            }
        }
        return true;
    }
}
