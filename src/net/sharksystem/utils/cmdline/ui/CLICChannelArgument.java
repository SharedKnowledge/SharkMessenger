package net.sharksystem.utils.cmdline.ui;

import net.sharksystem.SharkException;
import net.sharksystem.messenger.SharkMessengerChannel;

import java.io.IOException;

/**
 * Predefined argument for a SharkMessangerChannel retrieved through its uri and a peer
 */
public class CLICChannelArgument extends CLICArgument<SharkMessengerChannel>{

    @Override
    public boolean tryParse(String input) throws Exception {
        super.setEmptyStringAllowed(false);
        if(super.tryParse(input)) {
            try {
                this.parsedInput = CLIController.getModel()
                        .getMessengerComponent().getChannel(input);
                return true;
            } catch (SharkException | IOException e) {
                return false;
            }
        }
        return false;
    }
}
