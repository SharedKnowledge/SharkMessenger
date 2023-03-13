package net.sharksystem.utils.cmdline.control;

public class CLICBooleanArgument extends CLICArgument<Boolean> {
    @Override
    public boolean tryParse(String input) {
        this.parsedInput = Boolean.parseBoolean(input);
        return true;
    }
}