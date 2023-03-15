package net.sharksystem.utils.cmdline.control;

/**
 * An argument for string input
 */
public class CLICStringArgument extends CLICArgument<String> {

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
