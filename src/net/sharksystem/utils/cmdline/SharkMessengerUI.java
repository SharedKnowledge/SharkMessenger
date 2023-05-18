package net.sharksystem.utils.cmdline;

import net.sharksystem.SharkException;
import net.sharksystem.utils.Log;
import net.sharksystem.utils.cmdline.ui.CLIController;
import net.sharksystem.utils.cmdline.ui.CLIControllerInterface;
import net.sharksystem.utils.cmdline.ui.commands.general.CLICExit;
import net.sharksystem.utils.cmdline.ui.commands.general.CLICSaveLog;
import net.sharksystem.utils.cmdline.ui.commands.general.CLICShowLog;
import net.sharksystem.utils.cmdline.ui.commands.hubcontrol.CLICAddHubDescription;
import net.sharksystem.utils.cmdline.ui.commands.hubcontrol.CLICListHubDescription;
import net.sharksystem.utils.cmdline.ui.commands.messenger.*;
import net.sharksystem.utils.cmdline.ui.commands.pki.*;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class SharkMessengerUI {

    public static void main(String[] args) throws SharkException, FileNotFoundException {
        // re-direct asap/shark log messages
        PrintStream asapLogMessages = new PrintStream("asapLogMessages");
        Log.setOutStream(asapLogMessages);
        Log.setErrStream(asapLogMessages);

        SharkMessengerApp sharkMessengerApp = new SharkMessengerApp();
        //CLIModelInterface model = new CLIModel();

        // TODO: that's over-engineered. Controller code can be merged into this class - it just UI code
        // we use stdout to publish information
        CLIControllerInterface controller = new CLIController(System.out, sharkMessengerApp.getCLIModel());

        //General
        controller.addCommand(new CLICSaveLog(sharkMessengerApp, "saveLog", false));
        controller.addCommand(new CLICShowLog(sharkMessengerApp, "showLog", false));
        controller.addCommand(new CLICExit(sharkMessengerApp, "exit", false));

        //Messenger
        //controller.addCommand(new CLICAddPeer("mkPeer", true));
        //controller.addCommand(new CLICRunEncounter("runEncounter", true));
        //controller.addCommand(new CLICStopEncounter("stopEncounter", true));
        controller.addCommand(new CLICSendMessage(sharkMessengerApp, "sendMsg", true));
        controller.addCommand(new CLICGetMessages(sharkMessengerApp, "messages", true));
        controller.addCommand(new CLICGetHopList(sharkMessengerApp, "hops", true));
        controller.addCommand(new CLICCreateChannel(sharkMessengerApp, "mkCh", true));
        controller.addCommand(new CLICSetChannelAge(sharkMessengerApp, "setChAge", true));
        controller.addCommand(new CLICRemoveChannel(sharkMessengerApp, "rmCh", true));

        //PKI
        controller.addCommand(new CLICGetOwnerInfo(sharkMessengerApp, "ownerInfo", true));
        controller.addCommand(new CLICGetNumberOfKnownPeers(sharkMessengerApp, "numPeers", true));
        controller.addCommand(new CLICCreateNewKeyPair(sharkMessengerApp, "mkKeys", true));
        controller.addCommand(new CLICGetKeysCreationTime(sharkMessengerApp, "keysTime", true));
        controller.addCommand(new CLICGetCertificatesByIssuer(sharkMessengerApp, "certByIssuer", true));
        controller.addCommand(new CLICGetCertificatesBySubject(sharkMessengerApp, "certBySubject", true));
        controller.addCommand(new CLICGetIdentityAssurance(sharkMessengerApp, "ia", true));
        controller.addCommand(new CLICGetSigningFailureRate(sharkMessengerApp, "getSF", true));
        controller.addCommand(new CLICSetSigningFailureRate(sharkMessengerApp, "setSF", true));
        controller.addCommand(new CLICCreateCredentialMessage(sharkMessengerApp, "mkCredentialMsg", true));
        controller.addCommand(new CLICExchangeCertificates(sharkMessengerApp, "exchCert", true));
        controller.addCommand(new CLICGetCertificationPath(sharkMessengerApp, "certPath", true));

        // Hub control
        controller.addCommand(new CLICListHubDescription(sharkMessengerApp, "lsHubs", true));
        controller.addCommand(new CLICAddHubDescription(sharkMessengerApp,"addHub", true));
        /*
        controller.addCommand(new CLICRemoveHubDescription(sharkMessengerApp, "rmHub", true));
        controller.addCommand(new CLICConnectHub(sharkMessengerApp, "connectHub", true));
        controller.addCommand(new CLICReconnectHubs(sharkMessengerApp, "reconnectHubs", true));
         */

        controller.startCLI();
    }
}
