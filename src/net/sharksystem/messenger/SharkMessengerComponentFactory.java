package net.sharksystem.messenger;

import net.sharksystem.SharkComponent;
import net.sharksystem.SharkComponentFactory;
import net.sharksystem.pki.SharkPKIComponent;

public class SharkMessengerComponentFactory implements SharkComponentFactory {
    private final SharkPKIComponent pkiComponent;

    public SharkMessengerComponentFactory(SharkPKIComponent pkiComponent) {
        this.pkiComponent = pkiComponent;
    }

    @Override
    public SharkComponent getComponent() {
        return new SharkMessengerComponentImpl(pkiComponent);
    }
}
