package net.sharksystem.cmdline.sharkmessengerUI.commands.general;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;

/**
 * Command for terminating the messanger
 */
public class UICommandExit extends UICommand {


    public UICommandExit(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                         String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    public void execute() throws Exception {
        this.getPrintStream().println("TODO reimplement");
    }


    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Terminates the messanger.");
        return sb.toString();
    }

}
