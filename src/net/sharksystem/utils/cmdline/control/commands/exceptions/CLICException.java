package net.sharksystem.utils.cmdline.control.commands.exceptions;

public abstract class CLICException extends Exception {

    public CLICException(String errorMessage){
        super(errorMessage);
    }
}
