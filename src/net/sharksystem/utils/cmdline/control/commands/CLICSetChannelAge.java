package net.sharksystem.utils.cmdline.control.commands;

import net.sharksystem.messenger.SharkCommunicationAge;
import net.sharksystem.utils.cmdline.control.commands.exceptions.NotEnoughArgumentsSpecifiedException;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.util.List;

public class CLICSetChannelAge extends CLICommand{

    public CLICSetChannelAge(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model, List<String> args) throws Exception {
        if (args.size() >= 3) {
            String peer = args.get(0);
            String uri = args.get(1);
            SharkCommunicationAge age = SharkCommunicationAge.valueOf(args.get(2));
            model.getMessengerFromPeer(peer).getChannel(uri).setAge(age);
        } else {
            throw new NotEnoughArgumentsSpecifiedException("Expected three argument, but got less!");
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("changes the channel age");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
        //TODO: add detailed description
    }
}