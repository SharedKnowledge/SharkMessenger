package net.sharksystem.ui.messenger.cli;

public interface SharkNetMessengerSettings {
    /**
     * A hub can be remembered whenever a successful connection was established
     * @return remember or not
     */
    boolean getRememberNewHubConnections();
    void setRememberNewHubConnections(boolean rememberNewHubConnections);

    /**
     * This peer can try to reconnect to all known hubs while starting up
     * @return reconnects or not.
     */
    boolean getHubReconnect();
    void setHubReconnect(boolean hubReconnect);
}
