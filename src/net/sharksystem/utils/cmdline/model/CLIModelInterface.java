package net.sharksystem.utils.cmdline.model;

import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.messenger.SharkMessengerComponent;

public interface CLIModelInterface extends CLIModelObservable {

    void addPeer(String name, SharkTestPeerFS peer);

    boolean hasPeer(String name);

    SharkTestPeerFS getPeer(String name);

    SharkMessengerComponent getMessengerFromPeer(String name) throws SharkException;

    int getNextFreePortNumber();

    void addCommandToHistory(String commandIdentifier);

    String getCommandHistory();

    void terminate();

    void start();
}
