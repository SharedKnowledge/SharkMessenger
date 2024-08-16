package net.sharksystem.cmdline.sharkmessengerUI.commandarguments;

import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.persons.PersonValues;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;

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
    public boolean tryParse(String input) {
        super.setEmptyStringAllowed(false);
        if(super.tryParse(input)) {
            try {
                this.parsedInput = this.getSharkMessengerApp().getSharkPKIComponent().getPersonValuesByID(input);
            } catch (ASAPSecurityException e) {
                return false;
            }
            return true;
        }
        return false;
    }
}
