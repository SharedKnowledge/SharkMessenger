package net.sharksystem.messenger;

import net.sharksystem.SharkComponent;
import net.sharksystem.SharkComponentFactory;
import net.sharksystem.pki.SharkCertificateComponent;

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
