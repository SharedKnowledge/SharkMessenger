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
import net.sharksystem.cmdline.sharkmessengerUI.commands.tcp.UICommandCloseTCP;
import net.sharksystem.cmdline.sharkmessengerUI.commands.tcp.UICommandConnectTCP;
import net.sharksystem.cmdline.sharkmessengerUI.commands.tcp.UICommandOpenTCP;
import net.sharksystem.fs.ExtraData;
import net.sharksystem.fs.ExtraDataFS;
import net.sharksystem.utils.Log;

/**
 * This class is the entry point for the application.
 * Only commands a user should be able to execute are used below.
 */
public class ProductionUI {
    public static final String SETTINGSFILENAME = ".sharkMessengerSettings";
    public static final String PEERNAME_KEY = "peername";

    public static void main(String[] args) throws SharkException, IOException {
        String peerName = null;
        ExtraData settings = new ExtraDataFS("./" + SETTINGSFILENAME);
        boolean isBack = false;

        /**
         * possible arguments
         * peerName
         */
        switch(args.length) {
            case 0:
                break;
            case 1:
                peerName = args[0];
                break;
            default:
                System.out.println("possible arguments: ");
                System.out.println("\n -n peerName");
                System.exit(1);
                break;

        }

        System.out.println("Welcome to SharkMessenger version 0.1");
        if(peerName == null) {
            byte[] storedPeerNameBytes = settings.getExtra(PEERNAME_KEY);
            if(storedPeerNameBytes != null) {
                // we have a peer name
                peerName = new String(storedPeerNameBytes);
                isBack = true;
            }
        }

        if(peerName == null) {
            peerName = "";
            // ask for peer name
            do {
                System.out.print("Please enter peer name (must no be empty; first character is a letter): ");
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                    peerName = bufferedReader.readLine();
                } catch (IOException e) {
                    System.err.println(e.getLocalizedMessage());
                    System.exit(0);
                }
            } while (peerName.equals(""));
            // store it
            settings.putExtra(PEERNAME_KEY, peerName.getBytes());
        }

        // Re-direct asap/shark log messages.
        PrintStream asapLogMessages = new PrintStream("asapLogs" + peerName + ".txt");
        Log.setOutStream(asapLogMessages);
        Log.setErrStream(asapLogMessages);

        if(isBack) System.out.println("Welcome back " + peerName);
        else System.out.println("Welcome " + peerName);

        SharkMessengerApp sharkMessengerApp = new SharkMessengerApp(peerName, settings);
        SharkMessengerUI smUI = new SharkMessengerUI("", System.in, System.out, System.err, sharkMessengerApp);

        // General
        smUI.addCommand(new UICommandSaveLog(sharkMessengerApp, smUI, "saveLog", false));
        smUI.addCommand(new UICommandShowLog(sharkMessengerApp, smUI, "showLog", false));
        smUI.addCommand(new UICommandExit(sharkMessengerApp, smUI, "exit", false));
        smUI.addCommand(new UICommandDestroyPeer(sharkMessengerApp, smUI, "destroyPeer", false));

        // Direct connection control
        smUI.addCommand(new UICommandOpenTCP(sharkMessengerApp, smUI, "openTCP", false));
        smUI.addCommand(new UICommandConnectTCP(sharkMessengerApp, smUI, "connectTCP", false));
        smUI.addCommand(new UICommandCloseTCP(sharkMessengerApp, smUI, "closeTCPPort", false));
        smUI.addCommand(new UICommandShowOpenTCPPorts(sharkMessengerApp, smUI, "showOpenTCPPorts", false));

        // Messenger
        smUI.addCommand(new UICommandSendMessage(sharkMessengerApp, smUI, "sendMessage", true));
        smUI.addCommand(new UICommandListMessages(sharkMessengerApp, smUI, "lsMessages", true));
        smUI.addCommand(new UICommandGetMessageDetails(sharkMessengerApp, smUI, "getMessageDetails", true));
        smUI.addCommand(new UICommandListChannels(sharkMessengerApp, smUI, "lsChannel", true));
        smUI.addCommand(new UICommandCreateChannel(sharkMessengerApp, smUI, "mkChannel", true));
        smUI.addCommand(new UICommandSetChannelAge(sharkMessengerApp, smUI, "setChannelAge", true));
        smUI.addCommand(new UICommandRemoveChannel(sharkMessengerApp, smUI, "rmChannel", true));

        // PKI
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


        smUI.printUsage();
        smUI.runCommandLoop();
    }
}
