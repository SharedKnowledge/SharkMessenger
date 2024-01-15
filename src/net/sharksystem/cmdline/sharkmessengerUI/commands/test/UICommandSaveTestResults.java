package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import net.sharksystem.SharkException;
import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.messenger.SharkMessage;
import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerException;

/**
 * This command produces two csv verification files used for evaluating the test result. One for all sent and one for
 * all received messages.
 */
public class UICommandSaveTestResults extends UICommand {
    private final CharSequence peerName;
    private final UICommandStringArgument testID;

    public UICommandSaveTestResults(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                    String identifier, boolean rememberCommand) throws SharkException {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.peerName = sharkMessengerApp.getSharkPeer().getPeerID();
        this.testID = new UICommandStringArgument(sharkMessengerApp);
    }

    /**
     * Prepares the command with parsing the arguments (just one for this command).
     * @param arguments in following order:
     *                  0 testID
     * @return True if the arguments are parsable.
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.isEmpty()) {
            return false;
        }
        return this.testID.tryParse(arguments.get(0));
    }

    @Override
    protected void execute() throws ASAPSecurityException, IOException, SharkMessengerException {
        writeSentMessagesToFile(this.testID.getValue() + "_tx_" + peerName + ".csv");
        writeReceivedMessagesToFile(this.testID.getValue() + "_rx_" + peerName + ".csv");
    }

    private void writeSentMessagesToFile(String pathName) throws IOException {
        Path path = Paths.get(pathName);
        Files.write(path, "sender,receiver,uri,id".getBytes());
        List<MessageData> allSentMessageData = SentMessageCounter.getInstance(this.peerName).getSentMessageData();
        for (MessageData messageData : allSentMessageData) {
            String sender = this.peerName.toString();
            String receiver = messageData.receiver;
            String uri = messageData.channelUri;
            int id = messageData.messageID;
            Files.write(path, (System.lineSeparator() + sender + "," + receiver + "," + uri + "," + id)
                    .getBytes(), StandardOpenOption.APPEND);
        }
    }

    private void writeReceivedMessagesToFile(String pathName)
            throws IOException, SharkMessengerException, ASAPSecurityException {

        Path path = Paths.get(pathName);
        Files.write(path, "sender,receiver,uri,id,creationTime,receivedTime".getBytes());

        SharkMessengerComponent messenger = super.getSharkMessengerApp().getMessengerComponent();
        List<CharSequence> uris = messenger.getChannelUris();

        for (CharSequence uri : uris) {
            SharkMessageList messageList = messenger.getChannel(uri).getMessages();
            for (int i = 0; i < messageList.size(); i++) {
                SharkMessage message = messageList.getSharkMessage(i, true);
                int messageID = Integer.parseInt(getIDFromContent(message.getContent()));
                // write only messages where this peer is a recipient
                if (message.getRecipients().contains(this.peerName)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(System.lineSeparator());
                    sb.append(message.getSender());
                    sb.append(",");
                    sb.append(this.peerName);
                    sb.append(",");
                    sb.append(uri);
                    sb.append(",");
                    sb.append(messageID);
                    sb.append(",");
                    sb.append(message.getCreationTime());
                    sb.append(",");
                    sb.append(TestMessageReceivedListener.getInstance().getReceivedTime(uri, messageID));
                    Files.write(path, sb.toString().getBytes(), StandardOpenOption.APPEND);
                    // TODO: for tests with more than two peers and messages with more recipients or broadcasts this
                    //  format might not work for verification since every combination of recipients would need an own
                    //  ID count.
                }
            }
        }
    }

    private String getIDFromContent(byte[] content) {
       String contentString = new String(content, StandardCharsets.UTF_8);
       return contentString.split(System.lineSeparator())[0];
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }

    @Override
    public String getDescription() {
        return "Writes two csv files with information about all sent and received messages. Use for test verification.";
    }

}
