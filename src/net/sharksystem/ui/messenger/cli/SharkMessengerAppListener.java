package net.sharksystem.ui.messenger.cli;

abstract class SharkMessengerAppListener {
    protected final SharkMessengerApp sharkMessengerApp;

    SharkMessengerAppListener(SharkMessengerApp sharkMessengerApp) {
        this.sharkMessengerApp = sharkMessengerApp;
    }
}
