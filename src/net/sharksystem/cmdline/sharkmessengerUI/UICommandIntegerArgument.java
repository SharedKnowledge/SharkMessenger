package net.sharksystem.cmdline.sharkmessengerUI;

/**
 * Predefined argument for integer input
 */
public class UICommandIntegerArgument extends UICommandArgument<Integer> {
    public UICommandIntegerArgument(SharkMessengerApp sharkMessengerApp) {
        super(sharkMessengerApp);
    }

    @Override
    public boolean tryParse(String input) throws Exception {
        super.setEmptyStringAllowed(false);
        if(super.tryParse(input)) {
            try {
                this.parsedInput = Integer.parseInt(input);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
