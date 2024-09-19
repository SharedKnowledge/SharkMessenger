package net.sharksystem.ui.messenger.cli.commands.pki;

import java.util.Collection;

import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;

public class UICommandShowCertificatesBySubject extends AbstractCommandProduceCertificateListByPeerID {
    public UICommandShowCertificatesBySubject(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                             String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected Collection<ASAPCertificate> produceCertificateCollection(String peerID) throws ASAPSecurityException {
        return this.getSharkMessengerApp().getSharkPKIComponent().getCertificatesBySubject(peerID);
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("show all certificates issued for a specific peer (the subject).");
        // append hint for how to use
        return sb.toString();
    }
}