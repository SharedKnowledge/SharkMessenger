package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;

/**
 * This command resets the instance of the TestManager.
 */
public class UICommandResetTestManager extends UICommand {

    public UICommandResetTestManager(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
            String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    /**
     * Put the needed parameters in a list in following order:
     * <p>
     * none
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        return true;
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    protected void execute() throws Exception {
        TestManager.getInstance().resetManager();
    }

    @Override
    public String getDescription() {
        return "Resets the instance of TestManager.";
    }
    
}
