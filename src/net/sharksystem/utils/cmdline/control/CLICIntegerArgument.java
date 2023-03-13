package net.sharksystem.utils.cmdline.control;

public class CLICIntegerArgument extends CLICArgument<Integer> {
    @Override
    public boolean tryParse(String input) {
        try {
            this.parsedInput = Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
