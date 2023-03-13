package net.sharksystem.utils.cmdline.control;

public class CLICStringArgument extends CLICArgument<String> {

    @Override
    public boolean tryParse(String input) {
        if (input != null) {
            this.parsedInput = input;
            return true;
        } else {
            return false;
        }
    }
}
