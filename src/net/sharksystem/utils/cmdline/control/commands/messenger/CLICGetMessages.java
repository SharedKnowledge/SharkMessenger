package net.sharksystem.utils.cmdline.control.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.messenger.SharkMessage;
import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.utils.cmdline.control.*;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CLICGetMessages extends CLICommand {

    private final CLICSharkPeerArgument peer;
    private final CLICChannelArgument channel;

    public CLICGetMessages(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.peer = new CLICSharkPeerArgument();
        this.channel = new CLICChannelArgument(this.peer);
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Peer name: ", this.peer).
                addQuestion("Channel URI: ", this.channel).
                build();
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model) throws Exception {
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

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
        //TODO: add detailed description
    }
}
