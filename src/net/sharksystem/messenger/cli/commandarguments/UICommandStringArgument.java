package net.sharksystem.messenger.cli.commandarguments;

import net.sharksystem.messenger.cli.SharkMessengerApp;

/**
 * An argument for string input
 */
public class UICommandStringArgument extends UICommandArgument<String> {

    public UICommandStringArgument(SharkMessengerApp sharkMessengerApp) {
        super(sharkMessengerApp);
    }

    /**
     * @param input The user input.
     * @return false, if the input is empty
     */
    @Override
    public boolean tryParse(String input) {
        if(super.tryParse(input)) {
            this.parsedInput = input;
            return true;
        }
        return false;
    }
}
