package net.sharksystem.utils.cmdline;

import net.sharksystem.utils.cmdline.control.CLIController;
import net.sharksystem.utils.cmdline.control.CLIControllerInterface;
import net.sharksystem.utils.cmdline.control.commands.general.CLICExit;
import net.sharksystem.utils.cmdline.control.commands.general.CLICSaveLog;
import net.sharksystem.utils.cmdline.control.commands.general.CLICShowLog;
import net.sharksystem.utils.cmdline.control.commands.messenger.*;
import net.sharksystem.utils.cmdline.control.commands.pki.*;
import net.sharksystem.utils.cmdline.model.CLIModel;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;

public class CLIMain {

    public static void main(String[] args) {
        CLIModelInterface model = new CLIModel();

        CLIControllerInterface controller = new CLIController(model);

        //General
        controller.addCommand(new CLICSaveLog("saveLog", false));
        controller.addCommand(new CLICShowLog("showLog", false));
        controller.addCommand(new CLICExit("exit", true));

        //Messenger
        controller.addCommand(new CLICAddPeer("addPeer", true));
        controller.addCommand(new CLICRunEncounter("runEncounter", true));
        controller.addCommand(new CLICStopEncounter("stopEncounter", true));
        controller.addCommand(new CLICSendMessage("sendMessage", true));
        controller.addCommand(new CLICGetMessages("getMessages", true));
        controller.addCommand(new CLICGetHopList("getHops", true));
        controller.addCommand(new CLICCreateChannel("createChannel", true));
        controller.addCommand(new CLICSetChannelAge("setChannelAge", true));
        controller.addCommand(new CLICRemoveChannel("rmChannel", true));

        //PKI
        controller.addCommand(new CLICCreateCredentialMessage("mkCredentialMessage", true));
        controller.addCommand(new CLICCreateNewKeyPair("createKeyPair", true));
        controller.addCommand(new CLICExchangeCertificates("exchangeCert", true));
        controller.addCommand(new CLICGetCertificatesByIssuer("getCertByIssuer", true));
        controller.addCommand(new CLICGetCertificatesBySubject("getCertBySubject", true));
        controller.addCommand(new CLICGetCertificationPath("getCertPath", true));
        controller.addCommand(new CLICGetIdentityAssurance("getIA", true));
        controller.addCommand(new CLICGetKeysCreationTime("getKeysCreationTime", true));
        controller.addCommand(new CLICGetOwnerInfo("getOwnerInfo", true));
        controller.addCommand(new CLICGetSigningFailureRate("getSigningFailure", true));
        controller.addCommand(new CLICSendCredentialMessage("sendCredentialMessage", true));
        controller.addCommand(new CLICSetSigningFailureRate("setSigningFailure", true));

        controller.startCLI();
    }
}
