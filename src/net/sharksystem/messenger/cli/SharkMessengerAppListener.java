package net.sharksystem.messenger.cli;

abstract class SharkMessengerAppListener {
    protected final SharkMessengerApp sharkMessengerApp;

    SharkMessengerAppListener(SharkMessengerApp sharkMessengerApp) {
        this.sharkMessengerApp = sharkMessengerApp;
    }
}
