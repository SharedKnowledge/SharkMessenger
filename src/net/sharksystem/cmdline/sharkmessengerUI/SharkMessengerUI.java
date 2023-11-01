package net.sharksystem.cmdline.sharkmessengerUI;

import net.sharksystem.SharkException;
import net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol.*;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.*;
import net.sharksystem.cmdline.sharkmessengerUI.commands.pki.*;
import net.sharksystem.utils.Log;
import net.sharksystem.cmdline.sharkmessengerUI.commands.general.UICommandExit;
import net.sharksystem.cmdline.sharkmessengerUI.commands.general.UICommandSaveLog;
import net.sharksystem.cmdline.sharkmessengerUI.commands.general.UICommandShowLog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SharkMessengerUI {

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
        SharkMessengerUI smUI = new SharkMessengerUI(System.in, System.out, System.err, sharkMessengerApp);

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

    private final List<UICommand> commands = new ArrayList<>();
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
        for(UICommand command : this.commands) {
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

    public List<UICommand> getCommands() {
        return this.commands;
    }

    public void logQuestionAnswer(String userInput) {
        this.addCommandToHistory(userInput);
    }

    public void addCommand(UICommand command) {
        this.commands.add(command);

        // tell command its print stream
        command.setPrintStream(this.outStream);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                         actual user interface code                                             //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean letUserFillOutQuestionnaire(UICommandQuestionnaire questionnaire) {
        for (UICommandQuestion question : questionnaire.getQuestions()) {
            String userInput = "";
            try {
                do {
                    this.outStream.print(question.getQuestionText());
                    try {
                        userInput = bufferedReader.readLine();
                        if (userInput.equals(UICommandQuestionnaire.EXIT_SEQUENCE)) {
                            this.addCommandToHistory(UICommandQuestionnaire.EXIT_SEQUENCE);
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
        for (UICommand cmd : this.getCommands()) {
            int curLength = cmd.getIdentifier().length();
            if (curLength > longestCmd) {
                longestCmd = curLength;
            }
        }

        for (UICommand cmd : this.getCommands()) {
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
