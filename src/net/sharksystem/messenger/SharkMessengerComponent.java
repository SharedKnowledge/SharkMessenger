package net.sharksystem.messenger;

import net.sharksystem.ASAPFormats;
import net.sharksystem.SharkComponent;

/**
 * A decentralized messenger using ASAP - it does not require Internet access. It can take any
 * network, as unreliable as it might be, to transfer data.
 *
 * Message exchange is (optionally) signed and encrypted. It makes use of the ASAPCertificateExchange component.
 * It is essential part and first demonstrator of an Android Shark application.
 */
@ASAPFormats(formats = {SharkMessengerComponent.SHARK_MESSENGER_FORMAT})
public interface SharkMessengerComponent extends SharkComponent {
    String SHARK_MESSENGER_FORMAT = "shark/messenger";



}
