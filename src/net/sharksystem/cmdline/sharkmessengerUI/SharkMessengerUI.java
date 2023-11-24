package net.sharksystem.cmdline.sharkmessengerUI;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharkMessengerUI {

    private final Map<String,UICommand> commands = new HashMap<>();
    private final List<String> commandParameterStrings = new ArrayList<>();
    private final PrintStream outStream;
    private final PrintStream errStream;
    private final SharkMessengerApp sharkMessengerApp;
    private final BufferedReader bufferedReader;
    
    private boolean isInteractive = false;

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
        if (!commands.containsKey(commandIdentifier)) {
            this.commandNotFound(commandIdentifier);
            return;
        }

        UICommand command = this.commands.get(commandIdentifier);
        if (command.rememberCommand()) {
            StringBuilder sb = new StringBuilder();
            sb.append(command.getIdentifier());
            // append parameter
            for (String parameter : cmd) {
                sb.append(" ");
                sb.append(parameter);
            }
            this.addCommandToHistory(sb.toString());
        }
        
        if (isInteractive) {
            command.startCommandExecution();
        } else {
            command.initializeExecution(cmd);
        } 
    }

    //TODO: extract and set flags like -i for interactive mode
    public void setFlags(String[] args) {
        for (String argument : args) {
            argument.strip();
            final String hyphen = "-";

            if (argument.startsWith(hyphen)) {
                for (int i = 1; i < argument.length(); i++) {
                    char flag = argument.charAt(i);

                    switch (flag) {
                        case 'i':
                            this.isInteractive = true;
                    }
                }
            }
        }
    }

    public void addCommandToHistory(String commandIdentifier) {
        this.commandParameterStrings.add(commandIdentifier);
    }

    public List<String> getCommandHistory(){
        return this.commandParameterStrings;
    }

    public Map<String,UICommand> getCommands() {
        return this.commands;
    }

    public void logQuestionAnswer(String userInput) {
        this.addCommandToHistory(userInput);
    }

    public void addCommand(UICommand command) {
        this.commands.put(command.getIdentifier(), command);

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

    // would be nice to organize command list (group test commands)
    public void printUsage() {
        StringBuilder sb = new StringBuilder();

        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("COMMANDS:");
        sb.append(System.lineSeparator());

        int longestCmd = 0;
        for (UICommand cmd : this.getCommands().values()) {
            int curLength = cmd.getIdentifier().length();
            if (curLength > longestCmd) {
                longestCmd = curLength;
            }
        }

        for (UICommand cmd : this.getCommands().values()) {
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
