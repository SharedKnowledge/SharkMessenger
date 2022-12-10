package net.sharksystem.utils.cmdline.control.commands;

import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.io.IOException;
import java.util.List;

public class CLICCreateChannel extends CLICommand {

    public CLICCreateChannel(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model, List<String> args) throws Exception {
        if(args.size() >= 3) {
            String peerName = args.get(0);
            String channelURI = args.get(1);
            String channelName = args.get(2);

            if(model.hasPeer(peerName)) {
                SharkTestPeerFS peer = model.getPeer(peerName);

                try {
                    SharkMessengerComponent peerMessenger = (SharkMessengerComponent) peer.getComponent(SharkMessengerComponent.class);

                    if(args.size() >= 4) {
                        boolean mustNotExist = Boolean.parseBoolean(args.get(3));
                        peerMessenger.createChannel(channelURI, channelName, mustNotExist);
                    } else {
                        peerMessenger.createChannel(channelURI, channelName);
                    }

                } catch (SharkException | IOException e) {
                    ui.printError(e.getLocalizedMessage());
                }
            } else {
                ui.printError("Mentioned peer doesn't exists! Create peer first.");
            }
        } else {
            ui.printError("Not enough arguments specified!");
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creates a new channel.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
        //TODO: add detailed description
    }
}
