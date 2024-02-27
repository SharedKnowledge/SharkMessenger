package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandBooleanArgument;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;

/**
 * This command starts executing all commands given by a String
 * or File. The commands will be sequentialy executed in order.
 */
public class UICommandExecuteCommands extends UICommand {

    private final UICommandBooleanArgument inSteps;

    public UICommandExecuteCommands(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
            String identifier, boolean rememberCommand) {

        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        inSteps = new UICommandBooleanArgument(sharkMessengerApp);
    }

    /**
     * Put the needed parameters in a list in following order:
     * <p>
     * @param inSteps as boolean.
     * If this is true, every other command execution
     * waits for the user to press Enter before performing
     * the next command.
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.size() < 1) {
            return false;
        }

        return this.inSteps.tryParse(arguments.get(0));
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }

    @Override
    protected void execute() throws Exception {
        if (this.inSteps.getValue()) {
            executeInSteps();
            return;
        }

        SharkMessengerUI smUI = this.getSharkMessengerUI();
        List<String> commands = smUI.getParsedCommands();

        for (String command : commands) {
            smUI.handleUserInput(command);
        }
    }

    private void executeInSteps() throws Exception {
        InputStreamReader isr = new InputStreamReader(System.in);

        SharkMessengerUI smUI = this.getSharkMessengerUI();
        List<String> commands = smUI.getParsedCommands();

        for (String command : commands) {
            System.out.println("Execute with enter: " + command);
            isr.read();
            smUI.handleUserInput(command);
        }
    }

    @Override
    public String getDescription() {
        return "Executes a loaded test script.";
    }
    
}
