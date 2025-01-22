package net.sharksystem.ui.messenger.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import net.sharksystem.SharkException;
import net.sharksystem.ui.messenger.cli.commands.basics.*;
import net.sharksystem.ui.messenger.cli.commands.encounter.UICommandShowEncounter;
import net.sharksystem.ui.messenger.cli.commands.hubmanagement.UICommandListHub;
import net.sharksystem.ui.messenger.cli.commands.hubmanagement.UICommandStartHub;
import net.sharksystem.ui.messenger.cli.commands.hubmanagement.UICommandStopHub;
import net.sharksystem.ui.messenger.cli.commands.messenger.*;
import net.sharksystem.ui.messenger.cli.commands.persons.UICommandListPersons;
import net.sharksystem.ui.messenger.cli.commands.persons.UICommandRenamePerson;
import net.sharksystem.ui.messenger.cli.commands.persons.UICommandSetSigningFailure;
import net.sharksystem.ui.messenger.cli.commands.pki.*;
import net.sharksystem.ui.messenger.cli.commands.testing.UICommandSaveLog;
import net.sharksystem.ui.messenger.cli.commands.testing.UICommandShowLog;
import net.sharksystem.ui.messenger.cli.commands.tcp.UICommandCloseTCP;
import net.sharksystem.ui.messenger.cli.commands.tcp.UICommandConnectTCP;
import net.sharksystem.ui.messenger.cli.commands.tcp.UICommandOpenTCP;
import net.sharksystem.ui.messenger.cli.commands.tcp.UICommandShowOpenTCPPorts;
import net.sharksystem.fs.ExtraData;
import net.sharksystem.fs.ExtraDataFS;
import net.sharksystem.hub.peerside.ASAPHubManager;
import net.sharksystem.ui.messenger.cli.commands.hubaccess.*;
import net.sharksystem.utils.Log;

/**
 * This class is the entry point for the application.
 * Only commands a user should be able to execute are used below.
 *
 * (I have no idea if that class serves any reason any longer. thsc, Aug'24)
 */
public class ProductionUI {
    public static final String SETTINGSFILENAME = ".sharkMessengerSessionSettings";
    public static final String PEERNAME_KEY = "peername";
    public static final String SYNC_WITH_OTHERS_IN_SECONDS_KEY = "syncWithOthersInSeconds";

    public static void main(String[] args) throws SharkException, IOException {
        String peerName = null;
        int syncWithOthersInSeconds = ASAPHubManager.DEFAULT_WAIT_INTERVAL_IN_SECONDS;
        ExtraData sessionSettings = new ExtraDataFS("./" + SETTINGSFILENAME);
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
            case 2:
                peerName = args[0];
                try {
                    syncWithOthersInSeconds = Integer.parseInt(args[1]);
                }
                catch(NumberFormatException re) {
                    System.err.println("could not parse second parameter " +
                            "/ meant to be an integer telling how many seconds to wait for syncing with hubs"
                            + re.getLocalizedMessage());
                }
                break;
            default:
                System.out.println("possible arguments: ");
                System.out.println("\n peerName syncWithHubInSeconds");
                System.exit(1);
                break;

        }

        System.out.println("Welcome to SharkMessenger version 0.1");
        if(peerName == null) {
            try {
            byte[] storedPeerNameBytes = sessionSettings.getExtra(PEERNAME_KEY);
                // we have a peer name
                peerName = new String(storedPeerNameBytes);
                isBack = true;
            } catch(SharkException se) {
                // nothing from a previous session
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
            sessionSettings.putExtra(PEERNAME_KEY, peerName.getBytes());
        }

        sessionSettings.putExtra(SYNC_WITH_OTHERS_IN_SECONDS_KEY, syncWithOthersInSeconds);

        // Re-direct asap/shark log messages.
        PrintStream asapLogMessages = new PrintStream("asapLogs" + peerName + ".txt");
        Log.setOutStream(asapLogMessages);
        Log.setErrStream(asapLogMessages);

        if(isBack) System.out.println("Welcome back " + peerName);
        else System.out.println("Welcome " + peerName);

        SharkMessengerUI smUI = new SharkMessengerUI("", System.in, System.out, System.err);
        SharkMessengerApp sharkMessengerApp =
                new SharkMessengerApp(peerName, syncWithOthersInSeconds, System.out, System.err);

        // basics
        smUI.addCommand(new UICommandWait(sharkMessengerApp, smUI, "wait", false));
        smUI.addCommand(new UICommandEcho(sharkMessengerApp, smUI, "echo", false));
        smUI.addCommand(new UICommandMarkStep(sharkMessengerApp, smUI, "markstep", false));
        smUI.addCommand(new UICommandExit(sharkMessengerApp, smUI, "exit", false));
        smUI.addCommand(new UICommandDestroyPeer(sharkMessengerApp, smUI, "destroyPeer", false));

        // simple messenger
        smUI.addCommand(new UICommandListChannels(sharkMessengerApp, smUI, "lsChannel", true));
        smUI.addCommand(new UICommandCreateChannel(sharkMessengerApp, smUI, "mkChannel", true));
        smUI.addCommand(new UICommandRemoveChannelByIndex(sharkMessengerApp, smUI, "rmChannel", true));
        smUI.addCommand(new UICommandSendMessageExtended(sharkMessengerApp, smUI, "sendMessage", true));
        smUI.addCommand(new UICommandListMessages(sharkMessengerApp, smUI, "lsMessages", true));

        // TCP connection management
        smUI.addCommand(new UICommandOpenTCP(sharkMessengerApp, smUI, "openTCP", false));
        smUI.addCommand(new UICommandConnectTCP(sharkMessengerApp, smUI, "connectTCP", false));
        smUI.addCommand(new UICommandCloseTCP(sharkMessengerApp, smUI, "closeTCP", false));
        smUI.addCommand(new UICommandShowOpenTCPPorts(sharkMessengerApp, smUI, "showOpenTCPPorts", false));

        // encounter control
        smUI.addCommand(new UICommandShowEncounter(sharkMessengerApp, smUI, "lsEncounter", false));

        // Persons
        smUI.addCommand(new UICommandListPersons(sharkMessengerApp, smUI, "lsPersons", true));
        smUI.addCommand(new UICommandSetSigningFailure(sharkMessengerApp, smUI, "setSF", true));
        smUI.addCommand(new UICommandRenamePerson(sharkMessengerApp, smUI, "rnPerson", true));

        // PKI
        smUI.addCommand(new UICommandListCertificates(sharkMessengerApp, smUI, "lsCerts", true));
        smUI.addCommand(new UICommandShowCertificatesByIssuer(sharkMessengerApp, smUI, "certByIssuer", true));
        smUI.addCommand(new UICommandShowCertificatesBySubject(sharkMessengerApp, smUI, "certBySubject", true));
        smUI.addCommand(new UICommandShowPendingCredentials(sharkMessengerApp, smUI, "lsCredentials", true));
        smUI.addCommand(new UICommandSendCredentialMessage(sharkMessengerApp, smUI, "sendCredential", true));
        smUI.addCommand(new UICommandAcceptCredential(sharkMessengerApp, smUI, "acceptCredential", true));
        smUI.addCommand(new UICommandRefuseCredential(sharkMessengerApp, smUI, "refuseCredential", true));

        // Tests
        smUI.addCommand(new UICommandSaveLog(sharkMessengerApp, smUI, "saveLog", false));
        smUI.addCommand(new UICommandShowLog(sharkMessengerApp, smUI, "showLog", false));

        // hub access
        smUI.addCommand(new UICommandConnectHub(sharkMessengerApp, smUI, "connectHub", true));
        smUI.addCommand(new UICommandListConnectedHubs(sharkMessengerApp, smUI, "lsConnectedHubs", true));

        smUI.addCommand(new UICommandConnectHubFromDescriptionList(sharkMessengerApp, smUI, "connectHubFromList", true));
        smUI.addCommand(new UICommandListHubDescriptions(sharkMessengerApp, smUI, "lsHubDescr", true));
        smUI.addCommand(new UICommandAddHubDescription(sharkMessengerApp, smUI,"addHubDescr", true));
        smUI.addCommand(new UICommandRemoveHubDescription(sharkMessengerApp, smUI, "rmHubDescr", true));

        // hub management
        smUI.addCommand(new UICommandStartHub(sharkMessengerApp, smUI, "startHub", true));
        smUI.addCommand(new UICommandStopHub(sharkMessengerApp, smUI, "stopHub", true));
        smUI.addCommand(new UICommandListHub(sharkMessengerApp, smUI, "lsHubs", true));


        System.out.println("press ? or anything else to get a command list");
        smUI.runCommandLoop();
    }
}