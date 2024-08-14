package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.cmdline.sharkmessengerUI.*;

import java.util.Collection;
import java.util.List;

public abstract class AbstractCommandWithIndex extends UICommand {
    private UICommandIntegerArgument index;

    public AbstractCommandWithIndex(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                    String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        this.index = new UICommandIntegerArgument(sharkMessengerApp);
    }

    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.size() < 1) {
            this.getSharkMessengerApp().tellUI("index argument required");
            return false;
        } else {
            boolean isParsable = this.index.tryParse(arguments.get(0));
            if (!isParsable) {
                System.err.println("failed to parse index value" + arguments.get(0));
            }
            return isParsable;
        }
    }

    protected int getIndex() {
        return this.index.getValue();
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }
}