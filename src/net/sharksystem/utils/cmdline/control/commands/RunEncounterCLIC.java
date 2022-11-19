package net.sharksystem.utils.cmdline.control.commands;

import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.io.IOException;
import java.util.List;

public class RunEncounterCLIC extends CLICommand {


    public RunEncounterCLIC(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model, List<String> args) {
        if(args.size() >= 3) {
            String peerName1 = args.get(0);
            String peerName2 = args.get(1);
            boolean stopExchange = Boolean.getBoolean(args.get(2));

            if(model.hasPeer(peerName1) && model.hasPeer(peerName2)) {
                SharkTestPeerFS peer1 = model.getPeer(peerName1);
                SharkTestPeerFS peer2 = model.getPeer(peerName2);

                try {
                    peer1.getASAPTestPeerFS().startEncounter(model.getNextFreePortNumber(), peer2.getASAPTestPeerFS());
                } catch (SharkException | IOException e) {
                    ui.printError(e.getLocalizedMessage());
                }

                if(stopExchange) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored){}
                } else {
                    ui.printInfo("Connection was established. Stop the encounter with the stopEncounter command.");
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
        sb.append("runs an encounter between two peers");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
        //TODO: detailed description
    }
}
