package net.sharksystem.cmdline.sharkmessengerUI;

import net.sharksystem.SharkException;
import net.sharksystem.messenger.SharkMessengerChannel;

import java.io.IOException;

/**
 * Predefined argument for a SharkMessangerChannel retrieved through its uri and a peer
 */
public class UICommandChannelArgument extends UICommandArgument<SharkMessengerChannel> {

    public UICommandChannelArgument(SharkMessengerApp sharkMessengerApp) {
        super(sharkMessengerApp);
    }

    @Override
    public boolean tryParse(String input)  {
        super.setEmptyStringAllowed(false);
        if(super.tryParse(input)) {
            try {
                this.parsedInput = this.getSharkMessengerApp().getMessengerComponent().getChannel(input);
                return true;
            } catch (SharkException | IOException e) {
                return false;
            }
        }
        return false;
    }
}
