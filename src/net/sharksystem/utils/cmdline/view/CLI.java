package net.sharksystem.utils.cmdline.view;

import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.messenger.SharkMessage;
import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.messenger.SharkMessengerException;
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
    public CLI(InputStream in, PrintStream err, PrintStream out, CLIControllerStrategyInterface controller,
               CLIModelObservable model) {

        this.standardErr = err;
        this.standardOut = out;
        this.bufferedReader = new BufferedReader(new InputStreamReader(in));
        //redirect System.out here so that logging is better
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
            }
        }, false));

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
    private void printWelcome() {
        this.standardOut.println("Welcome to SharkMessenger version 0.1");
    }

    /**
     * Asks the user for a username. This will be the peer name.
     */
    @Override
    public String getUsername() {
        this.printWelcome();
        String username = "";
        do {
            this.print("Please enter your username: ");
            try {
                username = this.bufferedReader.readLine();
            } catch (IOException e) {
                this.printError(e.getLocalizedMessage());
            }
        } while (username.equals(""));
        return username;
    }


    /**
     * Prints all valid commands with their description
     */
    private void printUsage() {
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

        this.println(sb.toString());
    }


    /**
     * Starting the loop which acts like a listener to user input
     */
    public void runCommandLoop() {

        while (running) {
            try {
                this.standardOut.println();

                this.standardOut.print("Run a command by entering its name from the list above:");

                String userInputString = this.bufferedReader.readLine();
                this.printRecall(userInputString);

                if (userInputString != null) {
                    this.controller.handleUserInput(userInputString);
                }

            } catch (Exception e) {
                this.printError(e.getLocalizedMessage());
            }
        }
    }

    /**
     * Prints the output to the current line and terminates the line afterwards.
     *
     * @param output The String that should be written.
     */
    private synchronized void println(String output) {
        this.standardOut.println(output);
    }

    /**
     * Prints the output to the current line and without terminating the line
     *
     * @param output The String that should be written.
     */
    private synchronized void print(String output) {
        this.standardOut.print(output);
    }

    @Override
    public void printError(String error) {
        StringBuilder sb = new StringBuilder();
        sb.append("ERROR OCCURRED: ");
        sb.append(error);
        this.standardErr.println(sb);
    }

    private void printRecall(String output) {
        this.println("> " + output);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             Controller Information                                             //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void commandNotFound(String commandIdentifier) {
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown command: ");
        sb.append(commandIdentifier);
        sb.append(System.lineSeparator());
        sb.append("Please have a look at the following list of valid commands: ");
        this.println(sb.toString());
        this.printUsage();
    }

    @Override
    public void commandWasTerminated(String identifier) {
        this.println("The following command was terminated: " + identifier);
    }

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
    public boolean letUserFillOutQuestionnaire(CLICQuestionnaire questionnaire) {
        for (CLICQuestion question : questionnaire.getQuestions()) {
            String userInput = "";
            try {
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
            } catch (Exception e) {
                this.printError(e.getLocalizedMessage());
            }
            this.printRecall(userInput);
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
            sb.append("# RECEIVED CREDENTIAL MESSAGE");
            sb.append(System.lineSeparator());
            sb.append("# ");
            sb.append(subjectName);
            sb.append(System.lineSeparator());
            sb.append("# ");
            sb.append(subjectId);
            sb.append(System.lineSeparator());
            sb.append("# ");
            sb.append(validSince);
            sb.append(System.lineSeparator());
            sb.append("# ");
            sb.append(randomNumber);
            sb.append(System.lineSeparator());
            sb.append("# ");
            sb.append(message);
            sb.append(System.lineSeparator());
            sb.append("# ");
            sb.append(extraData);

            this.println(sb.toString());

        } catch (IOException e) {
            this.printError(e.getLocalizedMessage());
        }

    }

    @Override
    public void onChannelDisappeared(String channelUri) {
        this.printError("Couldn't find channel " + channelUri + " from which a message was received.");
    }

    @Override
    public void displayMessages(SharkMessageList messages) {
        try {
            SharkMessage msg;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < messages.size(); i++) {
                msg = messages.getSharkMessage(0, true);

                try {
                    String sender = msg.getSender().toString();
                    String messageText = Arrays.toString(msg.getContent());


                    sb.append("# RECEIVED MESSAGE");
                    sb.append(System.lineSeparator());
                    sb.append("#");
                    sb.append(System.lineSeparator());
                    sb.append("# Sender: ");
                    sb.append(sender);
                    sb.append(System.lineSeparator());
                    sb.append("# Message: ");
                    sb.append(messageText);
                    sb.append(System.lineSeparator());
                    sb.append("# Encrypted: ");
                    sb.append(msg.encrypted());
                    sb.append("\t");
                    sb.append("Signed: ");
                    sb.append(msg.verified());
                    sb.append(System.lineSeparator());
                    sb.append("# ");
                    sb.append(msg.getCreationTime().toString());

                    this.println(sb.toString());

                } catch (ASAPSecurityException e) {
                    this.printError("Couldn't decrypt message. This message is skipped.");
                }
            }
        } catch (IOException | SharkMessengerException | ASAPException e) {
            this.printError(e.getLocalizedMessage());
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             Model Observer Methods                                             //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void started() {
        this.running = true;
        this.printUsage();
        this.runCommandLoop();
    }

    @Override
    public void terminated() {
        this.running = false;
    }
}

