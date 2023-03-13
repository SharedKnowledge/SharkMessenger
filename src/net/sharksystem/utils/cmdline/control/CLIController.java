package net.sharksystem.utils.cmdline.control;

import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLI;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.util.ArrayList;
import java.util.List;

public class CLIController implements CLIControllerInterface, CLIControllerStrategyInterface {

    private final List<CLICommand> commands;
    private static CLIModelInterface model;
    private final CLIInterface view;

    public CLIController(CLIModelInterface cliModel) {
        model = cliModel;
        this.view = new CLI(System.in, System.err, System.out, this, model);
        this.commands = new ArrayList<>();
    }

    @Override
    public void handleUserInput(int commandIndex) throws Exception {
        //List<String> cmd = optimizeUserInputString(input);

        //the reason for removing the first argument (=command identifier) is that this here is the only
        //  place where it's needed. A method performing the action of a command only needs the arguments
        //  specified and not the command identifier
        //String commandIdentifier = cmd.remove(0);

        boolean validCommand = false;
        for (int i = 0; i < this.commands.size(); i++) {
            if (i == commandIndex) {
                validCommand = true;

                CLICommand command = this.commands.get(i);

                if (command.rememberCommand()) this.saveCommandInHistory(command.getIdentifier());

                command.startCommandExecution(this.view, model);

            }
        }

        //for(CLICommand command : this.commands) {
        //    if(command.getIdentifier().equals(commandIdentifier)) {
        //        validCommand = true;
        //        if (command.rememberCommand()) this.saveCommandInHistory(command.getIdentifier(), cmd);
        //        try {
        //            command.execute(this.view, this.model, cmd);
        //        } catch (Exception e) {
        //            this.view.exceptionOccurred(e);
        //        }
        //    }
        //}
        if (!validCommand) System.out.println("Unknown Command");
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
        model.start();
    }

    static CLIModelInterface getModel() {
        return model;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                    Helpers                                                     //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void saveCommandInHistory(String identifier) {
        StringBuilder sb = new StringBuilder();
        sb.append(identifier);

        this.model.addCommandToHistory(sb.toString());
    }

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
