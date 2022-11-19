package net.sharksystem.utils.cmdline.control.commands;

import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.io.IOException;
import java.util.List;

public class StopEncounterCLIC extends CLICommand {

    public StopEncounterCLIC(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model, List<String> args) {
        if(args.size() >= 2) {
            String peerName1 = args.get(0);
            String peerName2 = args.get(1);

            if(model.hasPeer(peerName1) && model.hasPeer(peerName2)) {
                SharkTestPeerFS peer1 = model.getPeer(peerName1);
                SharkTestPeerFS peer2 = model.getPeer(peerName2);

                try {
                    peer1.getASAPTestPeerFS().stopEncounter(peer2.getASAPTestPeerFS());
                } catch (SharkException | IOException e) {
                    ui.printError(e.getLocalizedMessage());
                }

            } else {
                ui.printError("Mentioned peer doesn't exists. Create peer first.");
            }

        } else {
            ui.printError("Not enough arguments mentioned!");
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("stops an already running encounter");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
        //TODO: add detailed description
    }
}
