package net.sharksystem.cmdline.sharkmessengerUI.commands.simpleMessenger;

import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPHop;
import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.messenger.*;
import net.sharksystem.utils.SerializationHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;

public class ChannelPrinter {
    private final SharkMessengerApp sharkMessengerApp;

    public ChannelPrinter(SharkMessengerApp sharkMessengerApp) {
        this.sharkMessengerApp = sharkMessengerApp;
    }

    public static void printChannelDescription(PrintStream ps, SharkMessengerChannel channel)
            throws IOException, SharkMessengerException {

        ps.print(channel.getName());
        ps.print(" | uri: ");
        ps.print(channel.getURI());
        ps.print(" | #messages: ");
        ps.print(channel.getMessages().size());
        ps.print(" | communication-age: ");
        SharkCommunicationAge age = channel.getAge();
        switch (age) {
            case BRONZE_AGE: ps.print("bronze"); break;
            case STONE_AGE: ps.print("stone"); break;
            case NETWORK_AGE: ps.print("network"); break;
            default: ps.print("unknown"); break;
        }
    }

    public static void printChannelDescriptions(
            PrintStream ps, SharkMessengerComponent messengerComponent, boolean printIndex)
                throws IOException, SharkMessengerException {

        List<CharSequence> channelUris = messengerComponent.getChannelUris();
        if(channelUris.isEmpty()) {
            ps.println("no channels");
        } else {
            int i = 0;
            for (CharSequence channelUri : channelUris) {
                if(printIndex) ps.print(i++ + ": ");
                SharkMessengerChannel channel = messengerComponent.getChannel(channelUri);
                printChannelDescription(ps, channel);
            }
            ps.print("\n");
        }
    }

    private static void printYesNo(PrintStream ps, boolean value) {
        if(value) ps.print("yes");
        else ps.print("no");
    }

    private String returnYesNo(boolean value) {
        if(value) return "yes";
        else return "no";
    }

    private static final String CHANNEL_PRINTER_LINE_SEPARATOR = "--------------------------------------------------------------------------------";
    public void printMessages(String channelUri, PrintStream ps, SharkMessageList messages)
            throws IOException, SharkMessengerException, ASAPException {

        this.sharkMessengerApp.tellUI("list messages in channel: " + channelUri);
        if(messages.size() < 1) {
            this.sharkMessengerApp.tellUI(": no messages");
        }
        else {
            for (int i = 0; i < messages.size(); i++) {
                this.sharkMessengerApp.tellUI("#" + i);
                this.sharkMessengerApp.tellUI(CHANNEL_PRINTER_LINE_SEPARATOR);
                SharkMessage message = messages.getSharkMessage(i, true);
                this.printMessageDetails(message);
                this.sharkMessengerApp.tellUI(CHANNEL_PRINTER_LINE_SEPARATOR);
            }
        }
    }

    public void printMessageDetails(SharkMessage message)
            throws IOException, ASAPException {

        StringBuilder sb = new StringBuilder();
        // content
        byte[] content = message.getContent();
        if(content.length < 1) {
            sb.append("no content");
        }
        sb.append("message content interpreted as String: ");
        sb.append(SerializationHelper.bytes2characterSequence(content).toString());
        sb.append("\n");

        // sender
        sb.append("sender: ");
        sb.append(message.getSender().toString());
        // recipients
        sb.append(" | recipients: ");
        Set<CharSequence> recipients = message.getRecipients();
        if(recipients.size() < 1) sb.append("not specified");
        boolean first = true;
        for(CharSequence recipient : recipients) {
            if(first) first = false;
            else sb.append(";");
            sb.append(recipient.toString());
        }

        // time
        sb.append(" | time: ");
        sb.append(DateTimeHelper.long2ExactTimeString(message.getCreationTime()));

        // encryption / verification
        sb.append("\n");
        sb.append("encrypted: ");
        sb.append(this.returnYesNo(message.encrypted()));

        sb.append(" | couldBeDecrypted: ");
        sb.append(this.returnYesNo(message.couldBeDecrypted()));

        sb.append(" | verified: ");
        sb.append(this.returnYesNo(message.verified()));

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
            sb.append("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }

        // content
        sb.append("\n");

        this.sharkMessengerApp.tellUI(sb.toString());
    }

    private void addHobDetails(StringBuilder sb, ASAPHop hop) {
        sb.append("sender: ");
        sb.append(hop.sender());
        sb.append(" | ");
        sb.append("encrypted: ");
        sb.append(this.returnYesNo(hop.encrypted()));
        sb.append(" | ");
        sb.append("verified: ");
        sb.append(this.returnYesNo(hop.verified()));
        sb.append(" | ");
        sb.append("via: ");
        switch(hop.getConnectionType()) {
            case INTERNET -> sb.append("DIRECT TCP");
            case ASAP_HUB -> sb.append("HUB");
            case AD_HOC_LAYER_2_NETWORK -> sb.append("Ad-HOC");
            case ONION_NETWORK -> sb.append("Onion");
        }
    }
}
