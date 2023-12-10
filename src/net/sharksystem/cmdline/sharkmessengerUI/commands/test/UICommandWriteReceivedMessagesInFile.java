package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import java.util.ArrayList;
import java.util.List;

import net.sharksystem.asap.ASAP;
import net.sharksystem.asap.ASAPMessages;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;
import net.sharksystem.messenger.SharkMessage;
import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.messenger.SharkMessengerComponent;

public class UICommandWriteReceivedMessagesInFile extends UICommand {

    public UICommandWriteReceivedMessagesInFile(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
            String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected boolean handleArguments(List<String> arguments) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleArguments'");
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }

    @Override
    protected void execute() throws Exception {
        List<byte[]> contentMessages = new ArrayList<>();

        SharkMessengerComponent messenger = super.getSharkMessengerApp().getMessengerComponent();
        List<CharSequence> uris = messenger.getChannelUris();

        for (CharSequence uri : uris) {
            SharkMessageList messages = messenger.getChannel(uri).getMessages();
            for (int position = 0; position < messages.size(); position++) {
                SharkMessage message = messages.getSharkMessage(position, true);
                contentMessages.add(message.getContent());
            }
        }

        //TODO: Write in file
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDescription'");
    }
    
}
