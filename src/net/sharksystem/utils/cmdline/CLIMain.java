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
        controller.addCommand(new CLICAddPeer("addPeer", true));
        controller.addCommand(new CLICExchangeCertificates("exchangeCert", true));
        controller.addCommand(new CLICRunEncounter("runEncounter", true));
        controller.addCommand(new CLICStopEncounter("stopEncounter", true));
        controller.addCommand(new CLICSendMessage("sendMessage", true));
        controller.addCommand(new CLICGetMessages("getMessage", true));
        controller.addCommand(new CLICGetHopList("getHops", true));
        controller.addCommand(new CLICCreateChannel("createChannel", true));
        controller.addCommand(new CLICSetChannelAge("setChannelAge", true));
        controller.addCommand(new CLICRemoveChannel("rmChannel", true));
        controller.addCommand(new CLICShowLog("showLog", false));
        controller.addCommand(new CLICSaveLog("saveLog", false));
        controller.addCommand(new CLICExit("exit", true));
        controller.startCLI();
    }
}
