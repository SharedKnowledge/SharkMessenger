package net.sharksystem.cmdline.sharkmessengerUI;

import net.sharksystem.asap.persons.PersonValues;

/**
 * An argument for SharkPeers
 */
public class UICommandKnownPeerArgument extends UICommandArgument<PersonValues> {

    public UICommandKnownPeerArgument(SharkMessengerApp sharkMessengerApp) {
        super(sharkMessengerApp);
    }

    /**
     * @param input The user input.
     * @return True, if the peer is known; False otherwise.
     */
    @Override
    public boolean tryParse(String input) throws Exception {
        super.setEmptyStringAllowed(false);
        if(super.tryParse(input)) {
            this.parsedInput = this.getSharkMessengerApp().getSharkPKIComponent().getPersonValuesByID(input);
            return true;
        }
        return false;
    }
}
