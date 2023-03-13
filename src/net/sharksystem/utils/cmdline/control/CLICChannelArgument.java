package net.sharksystem.utils.cmdline.control;

import net.sharksystem.SharkException;
import net.sharksystem.messenger.SharkMessengerChannel;

import java.io.IOException;

/**
 * Predefined argument for a SharkMessangerChannel retrieved through its uri.
 */
public class CLICChannelArgument extends CLICArgument<SharkMessengerChannel>{


    private final CLICSharkPeerArgument peerArgument;

    public CLICChannelArgument(CLICSharkPeerArgument peerArgument) {
        this.peerArgument = peerArgument;
    }

    @Override
    public boolean tryParse(String input) {
        try {
            this.parsedInput = CLIController.getModel().getMessengerFromPeer(this.peerArgument.getValue().getStatus().
                    name()).getChannel(input);
            return true;

        } catch (SharkException | IOException e) {
            return false;
        }
    }
}
