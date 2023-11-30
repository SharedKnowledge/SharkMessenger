package net.sharksystem.cmdline.sharkmessengerUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import net.sharksystem.SharkException;
import net.sharksystem.cmdline.sharkmessengerUI.commands.general.UICommandExit;
import net.sharksystem.cmdline.sharkmessengerUI.commands.general.UICommandSaveLog;
import net.sharksystem.cmdline.sharkmessengerUI.commands.general.UICommandShowLog;
import net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol.UICommandAddHubDescription;
import net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol.UICommandConnectHub;
import net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol.UICommandListConnectedHubs;
import net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol.UICommandListHubDescriptions;
import net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol.UICommandRemoveHubDescription;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandCreateChannel;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandGetMessageDetails;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandListChannels;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandListMessages;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandRemoveChannel;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandSendMessage;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandSetChannelAge;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandCreateCredentialMessage;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandCreateNewKeyPair;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandExchangeCertificates;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetCertificatesByIssuer;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetCertificatesBySubject;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetCertificationPath;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetIdentityAssurance;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetKeysCreationTime;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetNumberOfKnownPeers;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetOwnerInfo;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandGetSigningFailureRate;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.UICommandSetSigningFailureRate;
import net.sharksystem.utils.Log;

public class ProductionUI {
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
        SharkMessengerUI smUI = new SharkMessengerUI("", System.in, System.out, System.err, sharkMessengerApp);

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

        //controller.startCLI();

        smUI.printUsage();
        smUI.runCommandLoop();
    }
}
