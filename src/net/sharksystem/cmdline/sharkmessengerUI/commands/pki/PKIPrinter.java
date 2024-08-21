package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.pki.SharkPKIComponent;

public class PKIPrinter {
    private final SharkPKIComponent pki;

    public PKIPrinter(SharkPKIComponent pki) {
        this.pki = pki;
    }

    public String getIAString(CharSequence peerID) {
        StringBuilder sb = new StringBuilder();
        sb.append("ia (");
        sb.append(peerID);
        sb.append("): ");

        int ia = 0;
        try {
            ia = pki.getPersonValuesByID(peerID).getIdentityAssurance();
        } catch (ASAPSecurityException e) {
            // nothing found
            // is it you
            if(pki.getOwnerID().equals(peerID)) {
                ia = 10;
            }
        }
        sb.append(ia);
        sb.append(" (");
        if(ia == 0) sb.append("bad");
        else if(ia == 10) sb.append("perfect");
        else if(ia == 9) sb.append("good");
        else if(ia > 6) sb.append("nice");
        else if(ia < 4) sb.append("bad");
        else sb.append("enough?");
        sb.append(") ");

        return sb.toString();
    }
}
