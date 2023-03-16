package net.sharksystem.utils.cmdline.model;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.pki.SharkPKIComponent;

import java.util.List;

/**
 * Interface between the CLIModel and CLIController.
 * This interface provides all methods for the CLIController to get and set the state of the model.
 */
public interface CLIModelInterface extends CLIModelObservable {

    /**
     * @return Returns the messenger peer.
     */
    SharkPeerFS getPeer();

    /**
     * @return The SharkMessengerComponent from the local messenger peer.
     */
    SharkMessengerComponent getMessengerComponent();

    /**
     * Returns the SharkPKIComponent from the local peer.
     */
    SharkPKIComponent getPKIComponent();

    /**
     * Returns a free port number which can be used for a tcp connection.
     * @return Port number
     */
    int getNextFreePortNumber();

    /**
     * Adds a command by its identifier to the command history. The history saves the order of all valid user inputs,
     *  so that any scenario can be reconstructed.
     * @param commandIdentifier The command which was executed
     */
    void addCommandToHistory(String commandIdentifier);

    /**
     * Returns the command history as a string.
     * @return The command history.
     */
    String getCommandHistory();

    /**
     * Returns the command history as a list of strings.
     * @return The command history.
     */
    List<String> getCommandHistoryList();

    /**
     * Terminates the model which should inform any listeners.
     */
    void terminate();

    /**
     * Start the model which should inform any listeners.
     */
    void start() throws SharkException;
}
