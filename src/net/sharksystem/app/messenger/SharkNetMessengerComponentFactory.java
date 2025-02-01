package net.sharksystem.app.messenger;

import net.sharksystem.SharkComponent;
import net.sharksystem.SharkComponentFactory;
import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.pki.SharkPKIComponent;

import java.io.IOException;

public class SharkNetMessengerComponentFactory implements SharkComponentFactory {
    private final SharkPKIComponent pkiComponent;

    public SharkNetMessengerComponentFactory(SharkPKIComponent pkiComponent) {
        this.pkiComponent = pkiComponent;
    }

    @Override
    public SharkComponent getComponent(SharkPeer sharkPeer) {
        return new SharkNetMessengerComponentImpl(pkiComponent);
    }
}
