package net.sharksystem.utils.cmdline.view;

import net.sharksystem.utils.cmdline.control.CLIControllerStrategyInterface;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.model.CLIModelObservable;

import java.io.*;
import java.util.Scanner;

public class CLI implements CLIInterface, CLIModelStateObserver {
    private final PrintStream standardOut;
    private final PrintStream standardErr;
    private final Scanner scanner;

    private final CLIControllerStrategyInterface controller;
    private final CLIModelObservable model;

    private boolean running;


    /**
     * Constructor for the UI
     * @param in input stream from which the UI should read user input
     * @param err print stream for occurring errors
     * @param out print stream to write to
     */
    public CLI(InputStream in, PrintStream err, PrintStream out, CLIControllerStrategyInterface controller, CLIModelObservable model) {
        this.scanner = new Scanner(in);
        this.standardErr = err;
        this.standardOut = out;
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

        String number = String.valueOf(this.controller.getCommands().size());
        int chars = number.length();

        for(int i = 0; i < this.controller.getCommands().size(); i++) {
            sb.append(i);
            sb.append(" ".repeat(Math.max(0, chars - String.valueOf(i).length())));
            sb.append("\t");
            sb.append(this.controller.getCommands().get(i).getDescription());
            sb.append(System.lineSeparator());
        }

        this.standardOut.println(sb);
    }


    /**
     * Starting the loop which acts like a listener to user input
     */
    public void runCommandLoop() {

        while(running) {
            try {
                this.standardOut.println(System.lineSeparator());
                this.standardOut.print("Please select a command via an index: ");
                String userInputString = this.scanner.nextLine();

                if(userInputString != null) {
                    int commandIndex = Integer.parseInt(userInputString);
                    this.controller.handleUserInput(commandIndex);
                }

            } catch (NumberFormatException nfe) {
                this.standardErr.println("The given input can't be parsed to a number!");
                this.standardOut.println("Please input the corresponding number of the command you want to execute.");
            } catch (Exception e) {
                this.standardErr.println(e.getLocalizedMessage());
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             Controller Information                                             //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void printInfo(String information) {
        this.standardOut.println(information);
    }


    @Override
    public void printError(String error) {
        this.standardErr.println(error);
    }


    @Override
    public void letUserFillOutQuestionnaire(CLICQuestionnaire questionnaire) {
        questionnaire.start(this.standardOut, this.scanner);
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

