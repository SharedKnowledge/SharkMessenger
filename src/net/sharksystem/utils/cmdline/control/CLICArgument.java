package net.sharksystem.utils.cmdline.control;

/**
 * An argument for CLICCommands. It has the logic for parsing any user input (String) to the wished object type
 * specified. This separates the parsing from user input from other classes and preserves duplication of parsing code.
 * @param <T> The type of object you want to parse from the user input
 */
public abstract class CLICArgument<T> {

    /**
     * The parsed input from type T
     */
    protected T parsedInput;

    /**
     * @return the parsed input from type T
     */
    public T getValue() {
        return this.parsedInput;
    }

    /**
     * This function does the parsing and sets the parsed input if parsing was successful
     * @param input The user input.
     * @return true, if parsing was possible; false otherwise
     */
    public abstract boolean tryParse(String input);
}
