package net.sharksystem.utils.cmdline.control;

public abstract class CLICArgument<T> {

    protected T parsedInput;
    public T getValue() {
        return this.parsedInput;
    }

    public abstract boolean tryParse(String input);
}
