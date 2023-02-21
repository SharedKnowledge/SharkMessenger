package net.sharksystem.utils.cmdline.model;

import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.messenger.SharkMessengerComponent;

import java.util.List;

/**
 * Interface between the CLIModel and CLIController.
 * This interface provides all methods for the CLIController to get and set the state of the model
 */
public interface CLIModelInterface extends CLIModelObservable {

    /**
     * Adds a peer to the model
     * @param name The name of the peer
     * @param peer The peer
     */
    void addPeer(String name, SharkTestPeerFS peer);

    /**
     * Proves if a peer with the specified name already exists or not
     * @param name Name of the peer
     * @return True, if the peer already exits; False otherwise
     */
    boolean hasPeer(String name);

    /**
     * Returns a peer
     * @param name Name of the peer
     * @return Peer with the specified name
     */
    SharkTestPeerFS getPeer(String name);

    /**
     * Returns the SharkMessengerComponent form a peer
     * @param name Name of the peer
     * @return SharkMessengerComponent of that peer
     * @throws SharkException
     */
    SharkMessengerComponent getMessengerFromPeer(String name) throws SharkException;

    /**
     * Returns a free port number which can be used for a tcp connection
     * @return Port number
     */
    int getNextFreePortNumber();

    /**
     * Adds a command by its identifier to the command history. The history saves the order of all valid user inputs,
     *  so that any scenario can be reconstructed
     * @param commandIdentifier
     */
    void addCommandToHistory(String commandIdentifier);

    /**
     * Returns the command history as a string
     * @return The command history
     */
    String getCommandHistory();

    /**
     * Returns the command history as a list of strings
     * @return the command history
     */
    List<String> getCommandHistoryList();

    /**
     * Terminates the model which should inform any listeners
     */
    void terminate();

    /**
     * Start the model which should inform any listeners
     */
    void start();
}
