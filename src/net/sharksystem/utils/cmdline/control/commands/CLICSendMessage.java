package net.sharksystem.utils.cmdline.control.commands;

import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CLICSendMessage extends CLICommand {


    public CLICSendMessage(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model, List<String> args) throws Exception {

        if(args.size() >= 5) {
            String peerName = args.get(0);

            if(model.hasPeer(peerName)) {
                SharkTestPeerFS sender = model.getPeer(peerName);

                try {
                    SharkMessengerComponent messenger = (SharkMessengerComponent) sender.getComponent(SharkMessengerComponent.class);

                    CharSequence channelURI = args.get(1);
                    boolean sing = Boolean.parseBoolean(args.get(2));
                    boolean encrypt = Boolean.parseBoolean(args.get(3));
                    byte[] message = args.get(4).getBytes();

                    if(args.size() >= 6) {
                        Set<CharSequence> receivers = this.getAllExistingPeers(args.get(5), model);
                        messenger.sendSharkMessage(message, channelURI, receivers, sing, encrypt);

                    } else {
                        messenger.sendSharkMessage(message, channelURI, sing, encrypt);
                    }

                } catch (SharkException | IOException e) {
                    ui.printError(e.getLocalizedMessage());
                }
            } else {
                ui.printError("Mentioned peer doesn't exists! Create peer first");
            }
        } else {
            ui.printError("Not enough arguments mentioned!");
        }
    }

    private Set<CharSequence> getAllExistingPeers(String s, CLIModelInterface model) {
        Set<CharSequence> peers = new HashSet<>();
        String[] stringPeers = s.split(",");
        for (String peerName : stringPeers) {
            if (model.hasPeer(peerName)) {
                peers.add(peerName);
            }
        }
        return peers.size() > 0 ? peers : null;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("sends a message into a channel");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
        //TODO: add detailed description
    }
}
