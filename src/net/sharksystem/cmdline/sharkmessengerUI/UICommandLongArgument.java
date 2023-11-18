package net.sharksystem.cmdline.sharkmessengerUI;

/**
 * Predefined argument for long input
 */
public class UICommandLongArgument extends UICommandArgument<Long> {

    public UICommandLongArgument(SharkMessengerApp sharkMessengerApp) {
        super(sharkMessengerApp);
    }
    
    @Override
    public boolean tryParse(String input)  {
        super.setEmptyStringAllowed(false);
        if(super.tryParse(input)) {
            try {
                this.parsedInput = Long.parseLong(input);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
