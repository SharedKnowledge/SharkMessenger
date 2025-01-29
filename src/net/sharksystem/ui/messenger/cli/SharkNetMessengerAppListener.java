package net.sharksystem.ui.messenger.cli;

abstract class SharkNetMessengerAppListener {
    protected final SharkNetMessengerApp sharkMessengerApp;

    SharkNetMessengerAppListener(SharkNetMessengerApp sharkMessengerApp) {
        this.sharkMessengerApp = sharkMessengerApp;
    }
}
