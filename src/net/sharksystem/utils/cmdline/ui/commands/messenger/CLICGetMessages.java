package net.sharksystem.utils.cmdline.ui.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.messenger.SharkMessage;
import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CLICGetMessages extends CLICommand {

    private final CLICChannelArgument channel;

    public CLICGetMessages(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                           String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.channel = new CLICChannelArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder()
                .addQuestion("Channel URI: ", this.channel)
                .build();
    }

    @Override
    public void execute() throws Exception {
        try {
            SharkMessageList list = this.channel.getValue().getMessages();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                SharkMessage m = list.getSharkMessage(i, true);
                sb.append(i);
                sb.append("\t");
                sb.append(new String(m.getContent(), StandardCharsets.UTF_8));
                sb.append(System.lineSeparator());
            }
            ui.printInfo(sb.toString());

        } catch (SharkException | IOException e) {
            ui.printError(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Returns one or more messages a peer received.");
        return sb.toString();
    }

}
