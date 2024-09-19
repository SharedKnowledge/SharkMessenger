package net.sharksystem.ui.messenger.cli.commands.pki;

import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.crypto.ASAPCryptoAlgorithms;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.pki.SharkPKIComponent;

import java.security.NoSuchAlgorithmException;

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
            ia = pki.getIdentityAssurance(peerID);
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

    public String getCertificateAsString(ASAPCertificate cert) {
        StringBuilder sb = new StringBuilder();
        sb.append("issued by: ");
        sb.append(" name: ");
        sb.append(cert.getIssuerName());
        sb.append(" | id: ");
        sb.append(cert.getIssuerID());
        sb.append("\n");

        sb.append("for subject: ");
        sb.append(" name: ");
        sb.append(cert.getSubjectName());
        sb.append(" | id: ");
        sb.append(cert.getSubjectID());
        sb.append("\n");

        sb.append("valid since: ");
        sb.append(DateTimeHelper.long2DateString(cert.getValidSince().getTimeInMillis()));
        sb.append(" | valid until: ");
        sb.append(DateTimeHelper.long2DateString(cert.getValidUntil().getTimeInMillis()));
        sb.append("\n");

        sb.append("public key finger print: ");
        try {
            sb.append(ASAPCryptoAlgorithms.getFingerprint(cert.getPublicKey()));
        } catch (NoSuchAlgorithmException e) {
            sb.append(e.getLocalizedMessage());
        }

        return sb.toString();
    }
}
