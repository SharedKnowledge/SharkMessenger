package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import java.util.Collection;
import java.util.List;

import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaireBuilder;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandKnownPeerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;

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