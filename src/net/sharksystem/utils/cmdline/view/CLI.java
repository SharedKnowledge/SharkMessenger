package net.sharksystem.utils.cmdline.view;

import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.pki.CredentialMessage;
import net.sharksystem.utils.cmdline.control.*;
import net.sharksystem.utils.cmdline.model.CLIModelObservable;

import java.io.*;
import java.util.Arrays;

public class CLI implements CLIInterface, CLIModelStateObserver {
    private final PrintStream standardOut;
    private final PrintStream standardErr;

    private final BufferedReader bufferedReader;

    private final CLIControllerStrategyInterface controller;
    private final CLIModelObservable model;

    private boolean running;


    /**
     * Constructor for the UI
     *
     * @param in  input stream from which the UI should read user input
     * @param err print stream for occurring errors
     * @param out print stream to write to
     */
    public CLI(InputStream in, PrintStream err, PrintStream out, CLIControllerStrategyInterface controller, CLIModelObservable model) {
        this.standardErr = err;
        this.standardOut = out;
        this.bufferedReader = new BufferedReader(new InputStreamReader(in));
        //redirect System.out here so that logging is better
        //System.setOut(new PrintStream(new OutputStream() {
        //    @Override
        //    public void write(int b) {
        //    }
        //}, false));

        this.controller = controller;
        this.model = model;
        this.model.registerObserver(this);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                    Methods                                                     //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Print welcome message
     */
    public void printWelcome() {
        this.standardOut.println("Welcome to SharkMessenger version 0.1");
    }


    /**
     * Prints all valid commands with their description
     */
    public void printUsage() {
        StringBuilder sb = new StringBuilder();

        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("COMMANDS:");
        sb.append(System.lineSeparator());

        int longestCmd = 0;
        for (CLICommand cmd : this.controller.getCommands()) {
            int curLength = cmd.getIdentifier().length();
            if (curLength > longestCmd) {
                longestCmd = curLength;
            }
        }

        for (CLICommand cmd : this.controller.getCommands()) {
            sb.append(cmd.getIdentifier());
            sb.append(" ".repeat(Math.max(0, longestCmd - cmd.getIdentifier().length())));
            sb.append("\t");
            sb.append(cmd.getDescription());
            sb.append(System.lineSeparator());
        }

        this.standardOut.println(sb);
    }


    /**
     * Starting the loop which acts like a listener to user input
     */
    public void runCommandLoop() {

        while (running) {
            try {
                this.standardOut.println();

                this.standardOut.println("Run a command by entering its name from the list above:");

                String userInputString = this.bufferedReader.readLine();
                this.printInfo("INPUT: " + userInputString);

                if (userInputString != null) {
                    this.controller.handleUserInput(userInputString);
                }
            } catch (NumberFormatException nfe) {
                this.printError("Given input can't be parsed to a number!");
                this.printError("Please input the corresponding number of the command you want to execute.");
            } catch (Exception e) {
                this.printError(e.getLocalizedMessage());
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             Controller Information                                             //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void printInfo(String information) {
        StringBuilder sb = new StringBuilder();
        sb.append(" > ");
        sb.append(information);
        synchronized (this.standardOut) {
            this.standardOut.println(sb);
        }
    }


    @Override
    public void printError(String error) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ERROR: ");
        sb.append(error);
        this.standardErr.println(sb);
    }


    @Override
    public boolean letUserFillOutQuestionnaire(CLICQuestionnaire questionnaire) {
        for (CLICQuestion question : questionnaire.getQuestions()) {
            String userInput = "";
            do {
                this.standardOut.print(question.getQuestionText());
                try {
                    userInput = bufferedReader.readLine();
                    if (userInput.equals(CLICQuestionnaire.EXIT_SEQUENCE)) {
                        this.controller.logQuestionAnswer(CLICQuestionnaire.EXIT_SEQUENCE);
                        return false;
                    }
                } catch (IOException e) {
                    this.printError(e.getLocalizedMessage());
                }
            } while (!question.submitAnswer(userInput));
            this.printInfo("INPUT: " + userInput);
            this.controller.logQuestionAnswer(userInput);
        }
        return true;
    }

    @Override
    public void displayCredentialMessage(CredentialMessage credentialMessage) {
        try {
            CharSequence subjectName = credentialMessage.getSubjectName();
            CharSequence subjectId = credentialMessage.getSubjectID();
            String validSince = DateTimeHelper.long2DateString(credentialMessage.getValidSince());
            int randomNumber = credentialMessage.getRandomInt();
            String message = Arrays.toString(credentialMessage.getMessageAsBytes());
            String extraData = Arrays.toString(credentialMessage.getExtraData());

            StringBuilder sb = new StringBuilder();
            sb.append("########################################");
            sb.append(System.lineSeparator());
            sb.append("## ");
            sb.append(subjectName);
            sb.append(System.lineSeparator());
            sb.append("## ");
            sb.append(subjectId);
            sb.append(System.lineSeparator());
            sb.append("## ");
            sb.append(validSince);
            sb.append(System.lineSeparator());
            sb.append("## ");
            sb.append(randomNumber);
            sb.append(System.lineSeparator());
            sb.append("## ");
            sb.append(message);
            sb.append(System.lineSeparator());
            sb.append("## ");
            sb.append(extraData);

            synchronized (this.standardOut) {
                this.standardOut.println(sb);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             Model Observer Methods                                             //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void started() {
        this.running = true;
        this.printWelcome();
        this.printUsage();
        this.runCommandLoop();
    }

    @Override
    public void terminated() {
        this.running = false;
    }
}

