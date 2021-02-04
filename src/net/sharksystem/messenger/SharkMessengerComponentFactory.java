package net.sharksystem.messenger;

import net.sharksystem.SharkCertificateComponent;
import net.sharksystem.SharkComponent;
import net.sharksystem.SharkComponentFactory;

public class SharkMessengerComponentFactory implements SharkComponentFactory {
    private final SharkCertificateComponent certificateComponent;

    public SharkMessengerComponentFactory(SharkCertificateComponent certificateComponent) {
        this.certificateComponent = certificateComponent;
    }

    @Override
    public SharkComponent getComponent() {
        return new SharkMessengerComponentImpl(certificateComponent);
    }
}
