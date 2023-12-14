package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import net.sharksystem.SharkException;
import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.messenger.SharkMessage;
import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.messenger.SharkMessengerComponent;


public class UICommandSaveTestResults extends UICommand {
    private final String peerName;
    private final UICommandStringArgument testID;


    public UICommandSaveTestResults(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                    String identifier, boolean rememberCommand) throws SharkException {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.peerName = sharkMessengerApp.getSharkPeer().getPeerID().toString();
        this.testID = new UICommandStringArgument(sharkMessengerApp);
    }

    /**
     * Put the needed parameters in a list in following order:
     * <p>
     * @param testID as String
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.size() < 1) {
            return false;
        }
        return this.testID.tryParse(arguments.get(0));
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }

    @Override
    protected void execute() throws Exception {
        writeReceivedMessagesInFile(testID.getValue() + "_rx_"+ peerName+".csv");
        writeSentMessagesInFile(testID.getValue() + "_tx_"+ peerName+".csv");
    }
    private void writeSentMessagesInFile(String pathName) throws IOException {
        Path path = Paths.get(pathName);
        List<String> messages = SentMessageCounter.getInstance().getMessageData();
        Files.write(path, "sender,receiver,uri,id".getBytes());
        for (String message : messages) {
            Files.write(path,(System.lineSeparator() +peerName + "," + message).getBytes(), StandardOpenOption.APPEND);
        }

    }
    private void writeReceivedMessagesInFile(String pathName) throws Exception {
        Path path = Paths.get(pathName);
        SharkMessengerComponent messenger = super.getSharkMessengerApp().getMessengerComponent();
        List<CharSequence> uris = messenger.getChannelUris();

        Files.write(path, "sender,receiver,uri,id".getBytes());
        for (CharSequence uri : uris) {
            SharkMessageList messages = messenger.getChannel(uri).getMessages();
            for (int position = 0; position < messages.size(); position++) {
                SharkMessage message = messages.getSharkMessage(position, true);
                StringBuilder sb = new StringBuilder();
                sb.append(System.lineSeparator());
                sb.append(message.getSender().toString());
                sb.append(",");
                sb.append(this.peerName);
                sb.append(",");
                sb.append(uri.toString());
                sb.append(",");
                sb.append(getIDFromContent(message.getContent()));
                Files.write(path,sb.toString().getBytes(), StandardOpenOption.APPEND);
            }
        }

    }
    private String getIDFromContent(byte[] content) {
       String contentString = new String(content, StandardCharsets.UTF_8);
       return contentString.split(",")[0];
    }
    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDescription'");
    }
    
}
