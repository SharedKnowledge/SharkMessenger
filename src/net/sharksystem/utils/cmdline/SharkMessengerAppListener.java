package net.sharksystem.utils.cmdline;

abstract class SharkMessengerAppListener {
    protected final SharkMessengerApp sharkMessengerApp;

    SharkMessengerAppListener(SharkMessengerApp sharkMessengerApp) {
        this.sharkMessengerApp = sharkMessengerApp;
    }
}
