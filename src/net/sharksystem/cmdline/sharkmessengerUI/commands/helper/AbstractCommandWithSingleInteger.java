package net.sharksystem.cmdline.sharkmessengerUI.commands.helper;

import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandIntegerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaire;

import java.util.List;

public abstract class AbstractCommandWithSingleInteger extends UICommand {
    private UICommandIntegerArgument integerArgument;

    public AbstractCommandWithSingleInteger(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                            String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        this.integerArgument = new UICommandIntegerArgument(sharkMessengerApp);
    }

    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.size() < 1) {
            this.getSharkMessengerApp().tellUI("integer argument required");
            return false;
        } else {
            boolean isParsable = this.integerArgument.tryParse(arguments.get(0));
            if (!isParsable) {
                System.err.println("failed to parse integer value" + arguments.get(0));
            }
            return isParsable;
        }
    }

    protected int getIntegerArgument() {
        return this.integerArgument.getValue();
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }
}