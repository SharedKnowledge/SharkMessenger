package net.sharksystem.utils.cmdline.control.commands;

import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.pki.CredentialMessage;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.io.IOException;
import java.util.List;

public class ExchangeCertificatesCLIC extends CLICommand{

    public ExchangeCertificatesCLIC(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model, List<String> args) throws Exception {
        if (args.size() >= 2) {
            String peerName1 = args.get(0);
            String peerName2 = args.get(1);

            if(model.hasPeer(peerName1) && model.hasPeer(peerName2)) {
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
                    //TODO: add identity assurance or make extra command for that

                } catch (SharkException | IOException e) {
                    ui.printError(e.getLocalizedMessage());
                }


            } else {
                ui.printError("Mentioned peer doesn't exist. Create peer first.");
            }

        } else {
            ui.printError("Not enough arguments were mentioned");
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Exchanges the certificates between two peers");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        //TODO: detailed description
        return getDescription();
    }
}
