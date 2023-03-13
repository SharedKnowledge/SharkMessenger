package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.pki.CredentialMessage;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICStringArgument;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.io.IOException;

public class CLICExchangeCertificates extends CLICommand {

    private final CLICStringArgument peerName1;
    private final CLICStringArgument peerName2;

    public CLICExchangeCertificates(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.peerName1 = new CLICStringArgument();
        this.peerName2 = new CLICStringArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("First peer name: ", this.peerName1).
                addQuestion("Second peer name: ", this.peerName2).
                build();
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model) throws Exception {
        String peerName1 = this.peerName1.getValue();
        String peerName2 = this.peerName2.getValue();

        if (model.hasPeer(peerName1) && model.hasPeer(peerName2)) {
            SharkTestPeerFS peer1 = model.getPeer(peerName1);
            SharkTestPeerFS peer2 = model.getPeer(peerName2);

            try {
                SharkPKIComponent peer1PKI = (SharkPKIComponent) peer1.getComponent(SharkPKIComponent.class);
                SharkPKIComponent peer2PKI = (SharkPKIComponent) peer2.getComponent(SharkPKIComponent.class);

                // create credential messages
                CredentialMessage peer1CredentialMessage = peer1PKI.createCredentialMessage();
                CredentialMessage peer2CredentialMessage = peer2PKI.createCredentialMessage();

                // a) Alice and Bob exchange and accept credential messages and issue certificates
                ASAPCertificate peer1IssuedPeer2Certificate = peer1PKI.acceptAndSignCredential(peer2CredentialMessage);
                ASAPCertificate peer2IssuedPeer1Certificate = peer2PKI.acceptAndSignCredential(peer1CredentialMessage);

                //TODO: save certificates in model as they are likely needed again

            } catch (SharkException | IOException e) {
                ui.printError(e.getLocalizedMessage());
            }


        } else {
            ui.printError("Mentioned peer doesn't exist. Create peer first.");
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
}
