package net.sharksystem.ui.messenger.cli.commands.messenger;

import net.sharksystem.app.messenger.*;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPHop;
import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.ui.messenger.cli.commands.pki.PKIPrinter;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.SerializationHelper;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ChannelPrinter {
    public String getChannelDescription(SharkMessengerChannel channel)
            throws IOException, SharkMessengerException {

        StringBuilder sb = new StringBuilder();

        sb.append("name: ");
        sb.append(channel.getName());
        sb.append(" | uri: ");
        sb.append(channel.getURI());
        sb.append(" | #messages: ");
        sb.append(channel.getMessages().size());
        sb.append(" | age: ");
        SharkCommunicationAge age = channel.getAge();
        switch (age) {
            case BRONZE_AGE: sb.append("bronze"); break;
            case STONE_AGE: sb.append("stone"); break;
            case NETWORK_AGE: sb.append("network"); break;
            default: sb.append("unknown"); break;
        }

        return sb.toString();
    }

    public String getChannelDescriptions(SharkMessengerComponent messengerComponent)
                throws IOException, SharkMessengerException {

        StringBuilder sb = new StringBuilder();

        List<CharSequence> channelUris = messengerComponent.getChannelUris();
        if(channelUris.isEmpty()) {
            sb.append("no channels\n");
        } else {
            sb.append(channelUris.size());
            if(channelUris.size() > 1) sb.append(" channels:\n");
            else sb.append(" channel:\n");

            int i = 1;
            for (CharSequence channelUri : channelUris) {
                sb.append(i++ + ": ");
                SharkMessengerChannel channel = messengerComponent.getChannel(channelUri);
                sb.append(this.getChannelDescription(channel));
                sb.append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String returnYesNo(boolean value) {
        if(value) return "yes";
        else return "no";
    }

    private static final String CHANNEL_PRINTER_LINE_SEPARATOR = "\n--------------------------------------------------------------------------------\n";
    public String getMessagesASString(SharkPKIComponent pki, String channelUri, SharkMessageList messages)
            throws IOException, SharkMessengerException, ASAPException {

        StringBuilder sb = new StringBuilder();

        sb.append("list messages in channel: " + channelUri);
        if(messages.size() < 1) {
            sb.append(": no messages\n");
            return sb.toString();
        }
        else {
            sb.append("\n");
            for (int i = 0; i < messages.size(); i++) {
                sb.append("#" + i);
                sb.append(CHANNEL_PRINTER_LINE_SEPARATOR);
                SharkMessage message = messages.getSharkMessage(i, true);
                sb.append(this.getMessageDetails(pki, message));
                sb.append(CHANNEL_PRINTER_LINE_SEPARATOR);
            }
        }
        return sb.toString();
    }

    public String getMessageDetails(SharkPKIComponent pki, SharkMessage message)
            throws IOException, ASAPException {

        StringBuilder sb = new StringBuilder();
        // content
        byte[] content = message.getContent();
        if (content.length < 1) {
            sb.append("no content\n");
        } else {
            sb.append("message content interpreted as String: ");
            sb.append(SerializationHelper.bytes2characterSequence(content).toString());
            sb.append("\n");
        }

        // sender
        sb.append("sender: ");
        sb.append(message.getSender().toString());
        // recipients
        sb.append(" | recipients: ");
        Set<CharSequence> recipients = message.getRecipients();
        if (recipients.size() < 1) sb.append("not specified");
        boolean first = true;
        for (CharSequence recipient : recipients) {
            if (first) first = false;
            else sb.append(";");
            sb.append(recipient.toString());
        }

        // time
        sb.append(" | time: ");
        sb.append(DateTimeHelper.long2ExactTimeString(message.getCreationTime()));

        // encryption / verification
        sb.append("\n");
        sb.append("E2E security: encrypted: ");
        sb.append(this.returnYesNo(message.encrypted()));

        if (message.encrypted()) {
            sb.append(" | can decrypt: ");
            sb.append(this.returnYesNo(message.couldBeDecrypted()));
        }

        sb.append(" | ");
        sb.append("signed: ");
        sb.append(this.returnYesNo(message.signed()));
        if (message.signed()) {
            sb.append(" | verified: ");
            sb.append(this.returnYesNo(message.verified()));
            if(message.verified()) {
                sb.append(" | ");
                sb.append(new PKIPrinter(pki).getIAString(message.getSender()));
            }
        }

        // hoping list
        sb.append("\n");
        sb.append("hoping list: ");
        List<ASAPHop> asapHopsList = message.getASAPHopsList();
        if(asapHopsList.isEmpty()) {
            sb.append("no hops");
        } else {
            int i = 0;
            sb.append("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            for(ASAPHop hop : asapHopsList) {
                sb.append(i++);
                sb.append(": ");
                this.addHobDetails(sb, hop);
                sb.append("\n");
            }
            sb.append("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
        }

        //sb.append("\n");

        return sb.toString();
    }

    private void addHobDetails(StringBuilder sb, ASAPHop hop) {
        sb.append("sender: ");
        sb.append(hop.sender());
        sb.append(" | ");
        sb.append("P2P security: encrypted: ");
        sb.append(this.returnYesNo(hop.encrypted()));
        sb.append(" | ");
        sb.append("verified: ");
        sb.append(this.returnYesNo(hop.verified()));
        sb.append(" | ");
        sb.append("via: ");
        switch(hop.getConnectionType()) {
            case INTERNET -> sb.append("TCP");
            case ASAP_HUB -> sb.append("HUB");
            case AD_HOC_LAYER_2_NETWORK -> sb.append("Ad-Hoc");
            case ONION_NETWORK -> sb.append("Onion");
        }
    }
}
