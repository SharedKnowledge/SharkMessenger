package net.sharksystem.cmdline.sharkmessengerUI;

import java.io.*;

import net.sharksystem.SharkException;
import net.sharksystem.cmdline.sharkmessengerUI.commands.general.*;
import net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol.*;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.*;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.*;
import net.sharksystem.cmdline.sharkmessengerUI.commands.test.*;
import net.sharksystem.utils.Log;

public class TestUI {
    public static void main(String[] args) throws SharkException, IOException {
        // re-direct asap/shark log messages
        PrintStream asapLogMessages = new PrintStream("asapLogMessages.txt");
        Log.setOutStream(asapLogMessages);
        Log.setErrStream(asapLogMessages);

        // figure out user name
        System.out.println("Welcome to SharkMessenger version 0.1");
        String username = "";
        do {
            System.out.print("Please enter your username (must no be empty; first character is a letter): ");
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                username = bufferedReader.readLine();
            } catch (IOException e) {
                System.err.println(e.getLocalizedMessage());
                System.exit(0);
            }
        } while (username.equals(""));

        System.out.println("Welcome " + username);
        System.out.println("Startup your messenger instance");

        SharkMessengerApp sharkMessengerApp = new SharkMessengerApp(username);
        SharkMessengerUI smUI;

        // Instantiate SharkMessengerUI with or without a batch file for testing
        if (args.length != 0) {
            String filename = args[0];
            File file = new File(filename);
            smUI = new SharkMessengerUI(file, System.in, System.out, System.err, sharkMessengerApp);
        } else {
            smUI = new SharkMessengerUI("", System.in, System.out, System.err, sharkMessengerApp);
        }

        smUI.setFlags(args);
        //CLIModelInterface model = new CLIModel();

        // TODO: that's over-engineered. Controller code can be merged into this class - it just UI code
        // we use stdout to publish information
        //CLIControllerInterface smUI = new CLIController(System.out, sharkMessengerApp.getCLIModel());

        //General
        smUI.addCommand(new UICommandSaveLog(sharkMessengerApp, smUI, "saveLog", false));
        smUI.addCommand(new UICommandShowLog(sharkMessengerApp, smUI, "showLog", false));
        smUI.addCommand(new UICommandExit(sharkMessengerApp, smUI, "exit", false));

        //Messenger
        //controller.addCommand(new CLICAddPeer("mkPeer", true));
        //controller.addCommand(new CLICRunEncounter("runEncounter", true));
        //controller.addCommand(new CLICStopEncounter("stopEncounter", true));

        // messages
        smUI.addCommand(new UICommandSendMessage(sharkMessengerApp, smUI, "sendMessage", true));
        smUI.addCommand(new UICommandListMessages(sharkMessengerApp, smUI, "listMessages", true));
        smUI.addCommand(new UICommandGetMessageDetails(sharkMessengerApp, smUI, "getMessageDetails", true));

        // channels
        smUI.addCommand(new UICommandListChannels(sharkMessengerApp, smUI, "lsChannel", true));
        smUI.addCommand(new UICommandCreateChannel(sharkMessengerApp, smUI, "mkChannel", true));
        smUI.addCommand(new UICommandSetChannelAge(sharkMessengerApp, smUI, "setChAge", true));
        smUI.addCommand(new UICommandRemoveChannel(sharkMessengerApp, smUI, "rmCh", true));

        //PKI
        smUI.addCommand(new UICommandGetOwnerInfo(sharkMessengerApp, smUI, "ownerInfo", true));
        smUI.addCommand(new UICommandGetNumberOfKnownPeers(sharkMessengerApp, smUI, "numPeers", true));
        smUI.addCommand(new UICommandCreateNewKeyPair(sharkMessengerApp, smUI, "mkKeys", true));
        smUI.addCommand(new UICommandGetKeysCreationTime(sharkMessengerApp, smUI, "keysTime", true));
        smUI.addCommand(new UICommandGetCertificatesByIssuer(sharkMessengerApp, smUI, "certByIssuer", true));
        smUI.addCommand(new UICommandGetCertificatesBySubject(sharkMessengerApp, smUI, "certBySubject", true));
        smUI.addCommand(new UICommandGetIdentityAssurance(sharkMessengerApp, smUI, "ia", true));
        smUI.addCommand(new UICommandGetSigningFailureRate(sharkMessengerApp, smUI, "getSF", true));
        smUI.addCommand(new UICommandSetSigningFailureRate(sharkMessengerApp, smUI, "setSF", true));
        smUI.addCommand(new UICommandCreateCredentialMessage(sharkMessengerApp, smUI, "mkCredentialMsg", true));
        smUI.addCommand(new UICommandExchangeCertificates(sharkMessengerApp, smUI, "exchCert", true));
        smUI.addCommand(new UICommandGetCertificationPath(sharkMessengerApp, smUI, "certPath", true));

        // Hub control
        smUI.addCommand(new UICommandListHubDescriptions(sharkMessengerApp, smUI, "lsHubDescr", true));
        smUI.addCommand(new UICommandAddHubDescription(sharkMessengerApp, smUI,"addHubDescr", true));
        smUI.addCommand(new UICommandRemoveHubDescription(sharkMessengerApp, smUI, "rmHubDescr", true));
        smUI.addCommand(new UICommandListConnectedHubs(sharkMessengerApp, smUI, "lsHubs", true));
        smUI.addCommand(new UICommandConnectHub(sharkMessengerApp, smUI, "connectHub", true));
        /*
        smUI.addCommand(new CLICReconnectHubs(sharkMessengerApp, smUI, "reconnectHubs", true));
         */

        // Test
        // would be nice to organize ui command list (all test commands together)
        smUI.addCommand(new UICommandOpenTCP(sharkMessengerApp, smUI, "openTCP", true));
        // closeTCP is probably not an ideal name
        smUI.addCommand(new UICommandCloseTCP(sharkMessengerApp, smUI, "closeTCP", true));
        smUI.addCommand(new UICommandConnectTCP(sharkMessengerApp, smUI, "connectTCP", true));

        //controller.startCLI();

        smUI.printUsage();
        smUI.runCommandLoop();
    }
}
