package net.sharksystem.messenger.cli.commands.pki;

import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.messenger.cli.SharkMessengerApp;
import net.sharksystem.messenger.cli.SharkMessengerUI;

import java.util.Collection;

public class UICommandShowCertificatesByIssuer extends AbstractCommandProduceCertificateListByPeerID {
    public UICommandShowCertificatesByIssuer(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                             String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected Collection<ASAPCertificate> produceCertificateCollection(String peerID) throws ASAPSecurityException {
        return this.getSharkMessengerApp().getSharkPKIComponent().getCertificatesByIssuer(peerID);
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("show all certificates issued by a specific peer.");
        // append hint for how to use
        return sb.toString();
    }
}