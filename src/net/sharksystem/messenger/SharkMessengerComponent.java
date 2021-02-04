package net.sharksystem.messenger;

import net.sharksystem.ASAPFormats;
import net.sharksystem.SharkComponent;

@ASAPFormats(formats = {SharkMessengerComponent.SHARK_MESSENGER_FORMAT})
public interface SharkMessengerComponent extends SharkComponent {
    String SHARK_MESSENGER_FORMAT = "shark/messenger";


}
