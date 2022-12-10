package net.sharksystem.utils.cmdline.control.commands;

public class NotEnoughArgumentsSpecifiedException extends Exception {
    public NotEnoughArgumentsSpecifiedException(String s) {
        super(s);
    }
}
