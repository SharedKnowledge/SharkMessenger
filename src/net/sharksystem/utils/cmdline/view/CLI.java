package net.sharksystem.utils.cmdline.view;

import net.sharksystem.utils.cmdline.control.CLIControllerStrategyInterface;
import net.sharksystem.utils.cmdline.control.commands.CLICommand;
import net.sharksystem.utils.cmdline.model.CLIModelObservable;

import java.io.*;

public class CLI implements CLIInterface, CLIModelStateObserver {
    private final PrintStream standardOut;
    private final PrintStream standardErr;
    private final BufferedReader bufferedReader;

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
        this.bufferedReader = new BufferedReader(new InputStreamReader(in));
        this.standardErr = err;
        this.standardOut = out;
        //redirect System.in here so that logging is better

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

        for(CLICommand command : this.controller.getCommands()) {
            sb.append(command.getIdentifier());
            sb.append("\t\t\t");
            sb.append(command.getDescription());
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
                String userInputString = this.bufferedReader.readLine();

                if(userInputString != null) {

                    this.controller.handleUserInput(userInputString);
                }

            } catch (IOException e) {
                this.standardErr.println("Error: cannot read from input stream");
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
    public void exceptionOccurred(Exception exception) {
        this.standardErr.println(exception.getLocalizedMessage());
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

