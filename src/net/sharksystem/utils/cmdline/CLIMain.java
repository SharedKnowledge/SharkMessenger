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
        controller.addCommand(new CLICExit("exit", false));

        //Messenger
        controller.addCommand(new CLICAddPeer("mkPeer", true));
        controller.addCommand(new CLICRunEncounter("runEncounter", true));
        controller.addCommand(new CLICStopEncounter("stopEncounter", true));
        controller.addCommand(new CLICSendMessage("sendMsg", true));
        controller.addCommand(new CLICGetMessages("messages", true));
        controller.addCommand(new CLICGetHopList("hops", true));
        controller.addCommand(new CLICCreateChannel("mkCh", true));
        controller.addCommand(new CLICSetChannelAge("setChAge", true));
        controller.addCommand(new CLICRemoveChannel("rmCh", true));

        //PKI
        controller.addCommand(new CLICGetOwnerInfo("ownerInfo", true));
        controller.addCommand(new CLICCreateNewKeyPair("mkKeys", true));
        controller.addCommand(new CLICGetKeysCreationTime("keysTime", true));
        controller.addCommand(new CLICGetCertificatesByIssuer("certByIssuer", true));
        controller.addCommand(new CLICGetCertificatesBySubject("certBySubject", true));
        controller.addCommand(new CLICGetIdentityAssurance("ia", true));
        controller.addCommand(new CLICGetSigningFailureRate("getSF", true));
        controller.addCommand(new CLICSetSigningFailureRate("setSF", true));
        controller.addCommand(new CLICCreateCredentialMessage("mkCredentialMsg", true));
        controller.addCommand(new CLICExchangeCertificates("exchCert", true));
        controller.addCommand(new CLICGetCertificationPath("certPath", true));

        controller.startCLI();


    }
}
