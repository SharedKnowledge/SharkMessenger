package net.sharksystem.app.messenger;

import net.sharksystem.asap.listenermanager.GenericListenerImplementation;
import net.sharksystem.asap.listenermanager.GenericNotifier;

public class SharkNetMessagesReceivedListenerManager extends GenericListenerImplementation<SharkNetMessagesReceivedListener> {
    public void addSharkMessagesReceivedListener(SharkNetMessagesReceivedListener listener) {
        this.addListener(listener);
    }

    public void removeSharkMessagesReceivedListener(SharkNetMessagesReceivedListener listener) {
        this.removeListener(listener);
    }

    protected void notifySharkMessageReceivedListener(
            CharSequence uri) {

        SharkMessagesReceivedNotifier sharkMessagesReceivedNotifier =
                new SharkMessagesReceivedNotifier(uri);

        this.notifyAll(sharkMessagesReceivedNotifier, false);
    }

    private class SharkMessagesReceivedNotifier implements GenericNotifier<SharkNetMessagesReceivedListener> {
        private final CharSequence uri;

        public SharkMessagesReceivedNotifier(CharSequence uri) {
            this.uri = uri;
        }

        @Override
        public void doNotify(SharkNetMessagesReceivedListener sharkMessagesReceivedListener) {
            sharkMessagesReceivedListener.sharkMessagesReceived(this.uri);
        }
    }
}
