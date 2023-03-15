package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.SharkException;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.pki.CredentialMessage;
import net.sharksystem.pki.SharkCredentialReceivedListener;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.*;

import java.io.IOException;

public class CLICExchangeCertificates extends CLICommand implements SharkCredentialReceivedListener {

    private final CLICSharkPeerArgument peer1;
    private final CLICSharkPeerArgument peer2;

    public CLICExchangeCertificates(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.peer1 = new CLICSharkPeerArgument();
        this.peer2 = new CLICSharkPeerArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("First peer name: ", this.peer1).
                addQuestion("Second peer name: ", this.peer2).
                build();
    }

    @Override
    public void execute() throws Exception {
        try {
            SharkPKIComponent peer1PKI = model.getPKIFromPeer(this.peer1.getValue());
            SharkPKIComponent peer2PKI = model.getPKIFromPeer(this.peer2.getValue());

            // create credential messages
            CredentialMessage peer1CredentialMessage = peer1PKI.createCredentialMessage();
            CredentialMessage peer2CredentialMessage = peer2PKI.createCredentialMessage();

            // a) Alice and Bob exchange and accept credential messages and issue certificates
            ASAPCertificate peer1IssuedPeer2Certificate = peer1PKI.acceptAndSignCredential(peer2CredentialMessage);
            ASAPCertificate peer2IssuedPeer1Certificate = peer2PKI.acceptAndSignCredential(peer1CredentialMessage);

            //TODO: save certificates in model as they are likely needed again

            peer1PKI.setSharkCredentialReceivedListener(this);

        } catch (SharkException | IOException e) {
            ui.printError(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Exchanges the certificates between two peers.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }

    @Override
    public void credentialReceived(CredentialMessage credentialMessage) {

    }
}
