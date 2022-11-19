package net.sharksystem.utils.cmdline;

import net.sharksystem.utils.cmdline.control.CLIController;
import net.sharksystem.utils.cmdline.control.CLIControllerInterface;
import net.sharksystem.utils.cmdline.control.commands.*;
import net.sharksystem.utils.cmdline.model.CLIModel;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;

public class CLIMain {

    public static void main(String[] args) {
        CLIModelInterface model = new CLIModel();

        CLIControllerInterface controller = new CLIController(model);
        controller.addCommand(new AddPeerCLIC("addPeer", true));
        controller.addCommand(new ExchangeCertificatesCLIC("exchangeCert", true));
        controller.addCommand(new RunEncounterCLIC("runEncounter", true));
        controller.addCommand(new StopEncounterCLIC("stopEncounter", true));
        controller.addCommand(new SendMessageCLIC("sendMessage", true));
        controller.addCommand(new CreateChannelCLIC("createChannel", true));
        controller.addCommand(new RemoveChannelCLIC("rmChannel", true));
        controller.addCommand(new ExitCLIC("exit", true));
        controller.startCLI();
    }
}
