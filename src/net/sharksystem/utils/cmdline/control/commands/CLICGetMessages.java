package net.sharksystem.utils.cmdline.control.commands;

import net.sharksystem.SharkException;
import net.sharksystem.messenger.SharkMessage;
import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.messenger.SharkMessengerChannel;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CLICGetMessages extends CLICommand {

    public CLICGetMessages(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model, List<String> args) throws Exception {
        try {
            SharkMessengerComponent messenger = model.getMessengerFromPeer(args.get(0));
            SharkMessengerChannel channel = messenger.getChannel(args.get(1));
            SharkMessageList list = channel.getMessages();

            for (int i = 0; i < list.size(); i++) {
                SharkMessage m = list.getSharkMessage(i, true);
                ui.printInfo(new String(m.getContent(), StandardCharsets.UTF_8));
            }

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
