package net.sharksystem.cmdline.sharkmessengerUI.commands.helper;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandIntegerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaire;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandStringArgument;

import java.util.List;

public abstract class AbstractCommandWithSingleString extends UICommand {
    private String defaultString;
    private boolean optional;
    private String derString;
    private UICommandStringArgument stringArgument;

    public AbstractCommandWithSingleString(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                           String identifier, boolean rememberCommand) {
        this(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand, false, null);
    }

    public AbstractCommandWithSingleString(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                           String identifier, boolean rememberCommand, boolean optional, String defaultString) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        this.stringArgument = new UICommandStringArgument(sharkMessengerApp);
        this.optional = optional;
        this.defaultString = defaultString;
    }

    @Override
    protected boolean handleArguments(List<String> arguments) {
        this.derString = defaultString;
        if (arguments.size() < 1) {
            if(this.optional) return true;
            else this.getSharkMessengerApp().tellUI("string argument required");
        } else {
            boolean isParsable = this.stringArgument.tryParse(arguments.get(0));
            if (!isParsable) {
                if(this.optional) return true;
                System.err.println("failed to parse string value" + arguments.get(0));
            }
        }
        return false;
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