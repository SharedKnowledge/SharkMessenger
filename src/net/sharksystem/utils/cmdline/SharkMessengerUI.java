package net.sharksystem.utils.cmdline;

import net.sharksystem.SharkException;
import net.sharksystem.utils.Log;
import net.sharksystem.utils.cmdline.ui.CLICQuestion;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.ui.CLICommand;
import net.sharksystem.utils.cmdline.ui.commands.general.CLICExit;
import net.sharksystem.utils.cmdline.ui.commands.general.CLICSaveLog;
import net.sharksystem.utils.cmdline.ui.commands.general.CLICShowLog;
import net.sharksystem.utils.cmdline.ui.commands.hubcontrol.*;
import net.sharksystem.utils.cmdline.ui.commands.messenger.*;
import net.sharksystem.utils.cmdline.ui.commands.pki.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SharkMessengerUI {

    public static void main(String[] args) throws SharkException, FileNotFoundException {
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
        SharkMessengerUI smUI = new SharkMessengerUI(System.in, System.out, System.err, sharkMessengerApp);

        //CLIModelInterface model = new CLIModel();

        // TODO: that's over-engineered. Controller code can be merged into this class - it just UI code
        // we use stdout to publish information
        //CLIControllerInterface smUI = new CLIController(System.out, sharkMessengerApp.getCLIModel());

        //General
        smUI.addCommand(new CLICSaveLog(sharkMessengerApp, smUI, "saveLog", false));
        smUI.addCommand(new CLICShowLog(sharkMessengerApp, smUI, "showLog", false));
        smUI.addCommand(new CLICExit(sharkMessengerApp, smUI, "exit", false));

        //Messenger
        //controller.addCommand(new CLICAddPeer("mkPeer", true));
        //controller.addCommand(new CLICRunEncounter("runEncounter", true));
        //controller.addCommand(new CLICStopEncounter("stopEncounter", true));

        // messages
        smUI.addCommand(new CLICSendMessage(sharkMessengerApp, smUI, "sendMessage", true));
        smUI.addCommand(new CLICListMessages(sharkMessengerApp, smUI, "listMessages", true));
        smUI.addCommand(new CLICGetMessageDetails(sharkMessengerApp, smUI, "getMessageDetails", true));

        // channels
        smUI.addCommand(new CLICListChannels(sharkMessengerApp, smUI, "lsChannel", true));
        smUI.addCommand(new CLICCreateChannel(sharkMessengerApp, smUI, "mkChannel", true));
        smUI.addCommand(new CLICSetChannelAge(sharkMessengerApp, smUI, "setChAge", true));
        smUI.addCommand(new CLICRemoveChannel(sharkMessengerApp, smUI, "rmCh", true));

        //PKI
        smUI.addCommand(new CLICGetOwnerInfo(sharkMessengerApp, smUI, "ownerInfo", true));
        smUI.addCommand(new CLICGetNumberOfKnownPeers(sharkMessengerApp, smUI, "numPeers", true));
        smUI.addCommand(new CLICCreateNewKeyPair(sharkMessengerApp, smUI, "mkKeys", true));
        smUI.addCommand(new CLICGetKeysCreationTime(sharkMessengerApp, smUI, "keysTime", true));
        smUI.addCommand(new CLICGetCertificatesByIssuer(sharkMessengerApp, smUI, "certByIssuer", true));
        smUI.addCommand(new CLICGetCertificatesBySubject(sharkMessengerApp, smUI, "certBySubject", true));
        smUI.addCommand(new CLICGetIdentityAssurance(sharkMessengerApp, smUI, "ia", true));
        smUI.addCommand(new CLICGetSigningFailureRate(sharkMessengerApp, smUI, "getSF", true));
        smUI.addCommand(new CLICSetSigningFailureRate(sharkMessengerApp, smUI, "setSF", true));
        smUI.addCommand(new CLICCreateCredentialMessage(sharkMessengerApp, smUI, "mkCredentialMsg", true));
        smUI.addCommand(new CLICExchangeCertificates(sharkMessengerApp, smUI, "exchCert", true));
        smUI.addCommand(new CLICGetCertificationPath(sharkMessengerApp, smUI, "certPath", true));

        // Hub control
        smUI.addCommand(new CLICListHubDescriptions(sharkMessengerApp, smUI, "lsHubDescr", true));
        smUI.addCommand(new CLICAddHubDescription(sharkMessengerApp, smUI,"addHubDescr", true));
        smUI.addCommand(new CLICRemoveHubDescription(sharkMessengerApp, smUI, "rmHubDescr", true));
        smUI.addCommand(new CLICListConnectedHubs(sharkMessengerApp, smUI, "lsHubs", true));
        smUI.addCommand(new CLICConnectHub(sharkMessengerApp, smUI, "connectHub", true));
        /*
        smUI.addCommand(new CLICReconnectHubs(sharkMessengerApp, smUI, "reconnectHubs", true));
         */

        //controller.startCLI();

        smUI.printUsage();
        smUI.runCommandLoop();
    }

    private final List<CLICommand> commands = new ArrayList<>();
    private final List<String> commandStrings = new ArrayList<>();
    private final PrintStream outStream;
    private final PrintStream errStream;
    private final SharkMessengerApp sharkMessengerApp;
    private final BufferedReader bufferedReader;

    public SharkMessengerUI(InputStream is, PrintStream out, PrintStream err, SharkMessengerApp sharkMessengerApp) {
        this.outStream = out;
        this.errStream = err;
        this.sharkMessengerApp = sharkMessengerApp;
        this.bufferedReader = new BufferedReader(new InputStreamReader(is));
    }

    public void handleUserInput(String input) throws Exception {
        List<String> cmd = optimizeUserInputString(input);

        //the reason for removing the first argument (=command identifier) is that this here is the only
        //  place where it's needed. A method performing the action of a command only needs the arguments
        //  specified and not the command identifier
        String commandIdentifier = cmd.remove(0);

        boolean foundCommand = false;
        for(CLICommand command : this.commands) {
            if (command.getIdentifier().equals(commandIdentifier)) {
                foundCommand = true;
                if (command.rememberCommand()) {
                    this.addCommandToHistory(command.getIdentifier());
                }
                command.startCommandExecution();
            }
        }
        if(!foundCommand){
            this.commandNotFound(commandIdentifier);
        }
    }

    public void addCommandToHistory(String commandIdentifier) {
        this.commandStrings.add(commandIdentifier);
    }

    public List<CLICommand> getCommands() {
        return this.commands;
    }

    public void logQuestionAnswer(String userInput) {
        this.addCommandToHistory(userInput);
    }

    public void addCommand(CLICommand command) {
        this.commands.add(command);

        // tell command its print stream
        command.setPrintStream(this.outStream);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                         actual user interface code                                             //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean letUserFillOutQuestionnaire(CLICQuestionnaire questionnaire) {
        for (CLICQuestion question : questionnaire.getQuestions()) {
            String userInput = "";
            try {
                do {
                    this.outStream.print(question.getQuestionText());
                    try {
                        userInput = bufferedReader.readLine();
                        if (userInput.equals(CLICQuestionnaire.EXIT_SEQUENCE)) {
                            this.addCommandToHistory(CLICQuestionnaire.EXIT_SEQUENCE);
//                            this.controller.logQuestionAnswer(CLICQuestionnaire.EXIT_SEQUENCE);
                            return false;
                        }
                    } catch (IOException e) {
                        this.printError(e.getLocalizedMessage());
                    }
                } while (!question.submitAnswer(userInput));
            } catch (Exception e) {
                this.printError(e.getLocalizedMessage());
            }
            this.printRecall(userInput);
            this.addCommandToHistory(userInput);
        }
        return true;
    }

    private void printRecall(String output) {
        this.outStream.println("> " + output);
    }

    public void printError(String error) {
        this.errStream.println("exception: " + error);
    }

    public void commandWasTerminated(String identifier) {
        this.outStream.println("The following command was terminated: " + identifier);
    }


    public void commandNotFound(String commandIdentifier) {
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown command: ");
        sb.append(commandIdentifier);
        sb.append(System.lineSeparator());
        sb.append("Please have a look at the following list of valid commands: ");
        this.outStream.println(sb.toString());
        this.printUsage();
    }

    public PrintStream getOutStream() {
        return this.outStream;
    }

    public PrintStream getErrStream() {
        return this.errStream;
    }

    private void printUsage() {
        StringBuilder sb = new StringBuilder();

        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("COMMANDS:");
        sb.append(System.lineSeparator());

        int longestCmd = 0;
        for (CLICommand cmd : this.getCommands()) {
            int curLength = cmd.getIdentifier().length();
            if (curLength > longestCmd) {
                longestCmd = curLength;
            }
        }

        for (CLICommand cmd : this.getCommands()) {
            sb.append(cmd.getIdentifier());
            sb.append(" ".repeat(Math.max(0, longestCmd - cmd.getIdentifier().length())));
            sb.append("\t");
            sb.append(cmd.getDescription());
            sb.append(System.lineSeparator());
        }

        this.outStream.println(sb.toString());
    }

    public void runCommandLoop() {
        boolean running = true;
        while (running) {
            try {
                this.outStream.println();
                this.outStream.print("Run a command by entering its name from the list above:");

                String userInputString = this.bufferedReader.readLine();
                this.outStream.println("> " + userInputString);

                if (userInputString != null) {
                    this.handleUserInput(userInputString);
                }

            } catch (Exception e) {
                this.errStream.println("exception caught: " + e.getLocalizedMessage());
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                    Helpers                                                     //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Converts a string representing a command input by the user into a list of all command arguments.
     * All arguments are freed up from any spaces. All elements of the list are in lower case.
     * Example: (upper cases, too many spaces)
     * > gIt   hElp
     * > {"git", "help"}
     *
     * @param input the string inputted by the user
     * @return a list of all command arguments
     */
    private List<String> optimizeUserInputString(String input) {
        List<String> cmd = new ArrayList<>();
        final String space = " ";

        String[] unfinishedCmd = input.split(space);
        for (String attribute : unfinishedCmd) {
            //attribute = attribute.toLowerCase();
            attribute = attribute.trim();

            if (!attribute.equals(space)) {
                cmd.add(attribute);
            }
        }
        return cmd;
    }
}
