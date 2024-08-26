package net.sharksystem.messenger.cli.commandarguments;

import net.sharksystem.messenger.cli.SharkMessengerApp;

/**
 * Predefined argument for boolean
 */
public class UICommandBooleanArgument extends UICommandArgument<Boolean> {
    public UICommandBooleanArgument(SharkMessengerApp sharkMessengerApp) {
        super(sharkMessengerApp);
    }

    @Override
    public boolean tryParse(String input)  {
        super.setEmptyStringAllowed(false);
        if(super.tryParse(input)) {
            this.parsedInput = Boolean.parseBoolean(input);
            return true;
        } else {
            return false;
        }
    }
}
