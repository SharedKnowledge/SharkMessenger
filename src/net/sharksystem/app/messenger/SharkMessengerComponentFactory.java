package net.sharksystem.app.messenger;

import net.sharksystem.SharkComponent;
import net.sharksystem.SharkComponentFactory;
import net.sharksystem.SharkPeer;
import net.sharksystem.pki.SharkPKIComponent;

public class SharkMessengerComponentFactory implements SharkComponentFactory {
    private final SharkPKIComponent pkiComponent;

    public SharkMessengerComponentFactory(SharkPKIComponent pkiComponent) {
        this.pkiComponent = pkiComponent;
    }

    @Override
    public SharkComponent getComponent(SharkPeer sharkPeer) {
        return new SharkMessengerComponentImpl(pkiComponent);
    }
}
