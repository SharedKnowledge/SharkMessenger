package net.sharksystem.utils.cmdline.control.commands;

import net.sharksystem.asap.ASAPHop;
import net.sharksystem.utils.cmdline.control.commands.CLICommand;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.util.List;

public class CLICGetHopList extends CLICommand {

    public CLICGetHopList(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model, List<String> args) throws Exception {
        if (args.size() >= 1) {
            String peer = args.get(0);
            String uri = args.get(1);
            List<ASAPHop> hops  =
                    model.getMessengerFromPeer(peer)
                            .getChannel(uri)
                            .getMessages()
                            .getSharkMessage(0, true)
                            .getASAPHopsList();


            StringBuilder sb = new StringBuilder();
            hops.stream()
                    .forEach(hop -> {
                        sb.append(hop.sender());
                        if (hop != hops.get(hops.size() - 1)) {
                            sb.append(" -> ");
                        }
                    });
            ui.printInfo(sb.toString());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("returns the hops of a message");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
        //TODO: add detailed description
    }
}
