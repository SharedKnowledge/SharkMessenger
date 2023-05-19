package net.sharksystem.utils.cmdline.ui.commands.general;

import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.CLICommand;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaire;

/**
 * Command for terminating the messanger
 */
public class CLICExit extends CLICommand {


    public CLICExit(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                    String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    public void execute() throws Exception {
        model.terminate();
    }


    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Terminates the messanger.");
        return sb.toString();
    }

}
