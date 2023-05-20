package net.sharksystem.utils.cmdline.ui;

import net.sharksystem.SharkException;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLI;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

// Kann weg, inklusive der Interfaces, siehe Kommentare im Model.
// Die Nutzung des Command-Patterns für die Kommandos ist sehr nett.
public class CLIController implements CLIControllerInterface, CLIControllerStrategyInterface {

    private final List<CLICommand> commands;
    private static CLIModelInterface model;
    private static CLIInterface view;
    private static CLIController instance;
    private final PrintStream printStream;

    static CLIControllerInterface getController() {
        return CLIController.instance;
    }

    static CLIModelInterface getModel() {
        return CLIController.model;
    }

    static CLIInterface getView() {
        return CLIController.view;
    }
    
    public CLIController(PrintStream printOutStream, CLIModelInterface cliModel) {
        // TODO: setting a static member in each constructor... Makes no sense.
        CLIController.model = cliModel;
        CLIController.view = new CLI(System.in, System.err, System.out, this, CLIController.model);

        // define a print stream to let command show their results
        this.printStream = printOutStream;
        this.commands = new ArrayList<>();
        CLIController.instance = this;
    }

    @Override
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
                if (command.rememberCommand()) CLIController.model.addCommandToHistory(command.getIdentifier());
                command.startCommandExecution();
            }
        }
        if(!foundCommand){
            view.commandNotFound(commandIdentifier);
        }

    }

    @Override
    public List<CLICommand> getCommands() {
        return this.commands;
    }

    @Override
    public void logQuestionAnswer(String userInput) {
        CLIController.model.addCommandToHistory(userInput);
    }

    @Override
    public void addCommand(CLICommand command) {
        this.commands.add(command);

        // tell command its print stream
        command.setPrintStream(this.printStream);
    }

    @Override
    public void startCLI() throws SharkException {
        CLIController.model.start();
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
