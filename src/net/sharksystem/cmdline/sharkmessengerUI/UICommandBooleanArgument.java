package net.sharksystem.cmdline.sharkmessengerUI;

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
