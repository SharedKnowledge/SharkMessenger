package net.sharksystem.utils.cmdline.control;

import net.sharksystem.SharkTestPeerFS;

/**
 * An argument for SharkPeers
 */
public class CLICSharkPeerArgument extends CLICArgument<SharkTestPeerFS> {

    /**
     * @param input The user input.
     * @return true, if the peer with the specified name (input string) was already created; false otherwise
     */
    @Override
    public boolean tryParse(String input) {
        if(input != null) {
            if(CLIController.getModel().hasPeer(input)) {
                this.parsedInput = CLIController.getModel().getPeer(input);
                return true;
            }
        }
        return false;
    }
}
