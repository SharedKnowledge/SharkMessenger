package net.sharksystem.messenger;

import net.sharksystem.SharkComponent;
import net.sharksystem.SharkComponentFactory;
import net.sharksystem.pki.SharkPKIComponent;

public class SharkMessengerComponentFactory implements SharkComponentFactory {
    private final SharkPKIComponent pkiComponent;
//    private final SharkContactInformationComponent contactsComponent;

    /*
    public SharkMessengerComponentFactory(
            SharkPKIComponent pkiComponent, SharkContactInformationComponent contactsComponent) {

        this.pkiComponent = pkiComponent;
        this.contactsComponent = contactsComponent;
    }
     */

    public SharkMessengerComponentFactory(SharkPKIComponent pkiComponent) {
//        this(pkiComponent, null);
        this.pkiComponent = pkiComponent;
    }

    @Override
    public SharkComponent getComponent() {
        return new SharkMessengerComponentImpl(pkiComponent);
    }
}
