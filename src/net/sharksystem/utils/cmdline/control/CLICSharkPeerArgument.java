package net.sharksystem.utils.cmdline.control;

import net.sharksystem.SharkTestPeerFS;

public class CLICSharkPeerArgument extends CLICArgument<SharkTestPeerFS> {

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
