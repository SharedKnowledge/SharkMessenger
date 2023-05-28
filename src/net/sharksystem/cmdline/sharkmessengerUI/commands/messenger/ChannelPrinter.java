package net.sharksystem.cmdline.sharkmessengerUI.commands.messenger;

import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPHop;
import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.messenger.*;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

public class ChannelPrinter {
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

    public static void printMessages(PrintStream ps, SharkMessageList messages)
            throws IOException, SharkMessengerException, ASAPException {
        if(messages.size() < 1) ps.print("no messages");
        else {
            for (int i = 0; i < messages.size(); i++) {
                ps.println("#" + i++);
                ps.println("--------------------------------------------------------------------------------");
                SharkMessage message = messages.getSharkMessage(i, true);
                ChannelPrinter.printMessageDetails(ps, message);
                ps.println("--------------------------------------------------------------------------------");
            }
        }
    }
    public static void printMessageDetails(PrintStream ps, SharkMessage message)
            throws IOException, SharkMessengerException, ASAPException {

        ps.print("sender: ");
        ps.print(message.getSender());
        ps.print(" | recipients: ");
        Set<CharSequence> recipients = message.getRecipients();
        if(recipients.size() < 1) ps.print("not specified");
        boolean first = true;
        for(CharSequence recipient : recipients) {
            if(first) first = false;
            else ps.print(";");
            ps.print(recipient);
        }

        ps.print(" | time: ");
        ps.print(DateTimeHelper.long2ExactTimeString(message.getCreationTime()));

        // encryption
        ps.print("\n");
        ps.print("encrypted: ");
        printYesNo(ps, message.encrypted());

        ps.print(" | couldBeDecrypted: ");
        printYesNo(ps, message.couldBeDecrypted());

        ps.print(" | verified: ");
        printYesNo(ps, message.verified());

        // hoping list
        ps.print("\n");
        ps.print("hoping list: ");
        List<ASAPHop> asapHopsList = message.getASAPHopsList();
        if(asapHopsList.isEmpty()) {
            ps.print("no hops");
        } else {
            int i = 0;
            ps.print("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            for(ASAPHop hop : asapHopsList) {
                ps.print(i++ + ": ");
                ps.println(hop);
            }
            ps.print("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }


        // content
        ps.print("\n");
        byte[] content = message.getContent();
        if(content.length < 1) {
            ps.print("no content");
        }
        ps.print("message content interpreted as String:\n");
        ps.print(new String(content, StandardCharsets.UTF_8));
        ps.print("\n");
    }
}
