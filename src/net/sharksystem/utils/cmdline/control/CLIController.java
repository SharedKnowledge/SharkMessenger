package net.sharksystem.utils.cmdline.control;

import net.sharksystem.utils.cmdline.control.commands.CLICommand;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLI;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.util.ArrayList;
import java.util.List;

public class CLIController implements CLIControllerInterface, CLIControllerStrategyInterface {

    private final List<CLICommand> commands;
    private final CLIModelInterface model;
    private final CLIInterface view;

    public CLIController(CLIModelInterface model) {
        this.model = model;
        this.view = new CLI(System.in, System.err, System.out, this, this.model);
        this.commands = new ArrayList<>();
    }

    @Override
    public void handleUserInput(String input) {
        List<String> cmd = optimizeUserInputString(input);

        //the reason for removing the first argument (=command identifier) is that this here is the only
        //  place where it's needed. A method performing the action of a command only needs the arguments
        //  specified and not the command identifier
        String commandIdentifier = cmd.remove(0);

        for(CLICommand command : this.commands) {
            if(command.getIdentifier().equals(commandIdentifier)) {
                if (command.rememberCommand()) this.model.addCommandToHistory(command.getIdentifier());
                command.execute(this.view, this.model, cmd);
            }
        }
    }

    @Override
    public List<CLICommand> getCommands() {
        return this.commands;
    }

    @Override
    public void addCommand(CLICommand command) {
        this.commands.add(command);
    }

    @Override
    public void startCLI() {
        this.model.start();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                    Helpers                                                     //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Converts a string representing a command input by the user into a list of all command arguments.
     * All arguments are freed up from any spaces. All elements of the list is in lower case.
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
            attribute = attribute.toLowerCase();
            attribute = attribute.trim();

            if (!attribute.equals(space)) {
                cmd.add(attribute);
            }
        }
        return cmd;
    }
}
