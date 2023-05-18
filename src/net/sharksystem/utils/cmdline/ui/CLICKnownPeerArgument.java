package net.sharksystem.utils.cmdline.ui;

import net.sharksystem.asap.persons.PersonValues;

/**
 * An argument for SharkPeers
 */
public class CLICKnownPeerArgument extends CLICArgument<PersonValues> {

    /**
     * @param input The user input.
     * @return True, if the peer is known; False otherwise.
     */
    @Override
    public boolean tryParse(String input) throws Exception {
        super.setEmptyStringAllowed(false);
        if(super.tryParse(input)) {
            this.parsedInput = CLIController.getModel().getPKIComponent().getPersonValuesByID(input);
            return true;
        }
        return false;
    }
}
