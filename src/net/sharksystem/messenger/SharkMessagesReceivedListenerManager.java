package net.sharksystem.messenger;

import net.sharksystem.asap.listenermanager.GenericListenerImplementation;
import net.sharksystem.asap.listenermanager.GenericNotifier;

public class SharkMessagesReceivedListenerManager extends GenericListenerImplementation<SharkMessagesReceivedListener> {
    public void addSharkMessagesReceivedListener(SharkMessagesReceivedListener listener) {
        this.addListener(listener);
    }

    public void removeSharkMessagesReceivedListener(SharkMessagesReceivedListener listener) {
        this.removeListener(listener);
    }

    protected void notifySharkMessageReceivedListener(
            CharSequence uri) {

        SharkMessagesReceivedNotifier sharkMessagesReceivedNotifier =
                new SharkMessagesReceivedNotifier(uri);

        this.notifyAll(sharkMessagesReceivedNotifier, false);
    }

    private class SharkMessagesReceivedNotifier implements GenericNotifier<SharkMessagesReceivedListener> {
        private final CharSequence uri;

        public SharkMessagesReceivedNotifier(CharSequence uri) {
            this.uri = uri;
        }

        @Override
        public void doNotify(SharkMessagesReceivedListener sharkMessagesReceivedListener) {
            sharkMessagesReceivedListener.sharkMessagesReceived(this.uri);
        }
    }
}
