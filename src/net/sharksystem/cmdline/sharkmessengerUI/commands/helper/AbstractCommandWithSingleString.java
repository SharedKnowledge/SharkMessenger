package net.sharksystem.cmdline.sharkmessengerUI.commands.helper;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandIntegerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaire;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandStringArgument;

import java.util.List;

public abstract class AbstractCommandWithSingleString extends UICommand {
    private UICommandStringArgument stringArgument;

    public AbstractCommandWithSingleString(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                           String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        this.stringArgument = new UICommandStringArgument(sharkMessengerApp);
    }

    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.size() < 1) {
            this.getSharkMessengerApp().tellUI("string argument required");
            return false;
        } else {
            boolean isParsable = this.stringArgument.tryParse(arguments.get(0));
            if (!isParsable) {
                System.err.println("failed to parse string value" + arguments.get(0));
            }
            return isParsable;
        }
    }

    protected String getStringArgument() {
        return this.stringArgument.getValue();
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }
}