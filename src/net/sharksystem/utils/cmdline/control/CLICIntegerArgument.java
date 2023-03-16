package net.sharksystem.utils.cmdline.control;

/**
 * Predefined argument for integer input
 */
public class CLICIntegerArgument extends CLICArgument<Integer> {
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
