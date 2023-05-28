package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.messenger.SharkMessage;
import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.messenger.SharkMessengerException;
import net.sharksystem.pki.CredentialMessage;

import java.io.*;
import java.util.Arrays;

public class CredentialPrinterHelper {

    public void displayCredentialMessage(CredentialMessage credentialMessage) {
        try {
            CharSequence subjectName = credentialMessage.getSubjectName();
            CharSequence subjectId = credentialMessage.getSubjectID();
            String validSince = DateTimeHelper.long2DateString(credentialMessage.getValidSince());
            int randomNumber = credentialMessage.getRandomInt();
            String message = Arrays.toString(credentialMessage.getMessageAsBytes());
            String extraData = Arrays.toString(credentialMessage.getExtraData());

            StringBuilder sb = new StringBuilder();
            sb.append("# RECEIVED CREDENTIAL MESSAGE");
            sb.append(System.lineSeparator());
            sb.append("# ");
            sb.append(subjectName);
            sb.append(System.lineSeparator());
            sb.append("# ");
            sb.append(subjectId);
            sb.append(System.lineSeparator());
            sb.append("# ");
            sb.append(validSince);
            sb.append(System.lineSeparator());
            sb.append("# ");
            sb.append(randomNumber);
            sb.append(System.lineSeparator());
            sb.append("# ");
            sb.append(message);
            sb.append(System.lineSeparator());
            sb.append("# ");
            sb.append(extraData);

//            this.println(sb.toString());

        } catch (IOException e) {
  //          this.printError(e.getLocalizedMessage());
        }

    }

    public void displayMessages(SharkMessageList messages) {
        try {
            SharkMessage msg;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < messages.size(); i++) {
                msg = messages.getSharkMessage(0, true);

                try {
                    String sender = msg.getSender().toString();
                    String messageText = Arrays.toString(msg.getContent());


                    sb.append("# RECEIVED MESSAGE");
                    sb.append(System.lineSeparator());
                    sb.append("#");
                    sb.append(System.lineSeparator());
                    sb.append("# Sender: ");
                    sb.append(sender);
                    sb.append(System.lineSeparator());
                    sb.append("# Message: ");
                    sb.append(messageText);
                    sb.append(System.lineSeparator());
                    sb.append("# Encrypted: ");
                    sb.append(msg.encrypted());
                    sb.append("\t");
                    sb.append("Signed: ");
                    sb.append(msg.verified());
                    sb.append(System.lineSeparator());
                    sb.append("# ");
                    sb.append(DateTimeHelper.long2ExactTimeString(msg.getCreationTime()));

//                    this.println(sb.toString());

                } catch (ASAPSecurityException e) {
  //                  this.printError("Couldn't decrypt message. This message is skipped.");
                }
            }
        } catch (IOException | SharkMessengerException | ASAPException e) {
    //        this.printError(e.getLocalizedMessage());
        }
    }
}

