package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.pki.CredentialMessage;
import net.sharksystem.pki.SharkCredentialReceivedListener;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;

public class UICommandExchangeCertificates extends UICommand implements SharkCredentialReceivedListener {
    private final UICommandKnownPeerArgument subject;

    public UICommandExchangeCertificates(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                         String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.subject = new UICommandKnownPeerArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("Second peer name: ", this.subject)
                .build();
    }

    @Override
    public void execute() throws Exception {
        //try {
            //SharkPKIComponent pki = model.getPKIComponent();

            //StringBuilder sb = new StringBuilder();
            //sb.append("Peer \"");
            //sb.append(input);
            //sb.append("\" wasn't created yet!");
            //sb.append(System.lineSeparator());
            //sb.append("Enter the name of an existing peer or terminate this command by entering: ");
            //sb.append(CLICQuestionnaire.EXIT_SEQUENCE);
            //CLIController.getView().printInfo(sb.toString());

            // create credential messages
            //CredentialMessage peer1CredentialMessage = peer1PKI.createCredentialMessage();
            //CredentialMessage peer2CredentialMessage = peer2PKI.createCredentialMessage();

            // a) Alice and Bob exchange and accept credential messages and issue certificates
            //ASAPCertificate peer1IssuedPeer2Certificate = peer1PKI.acceptAndSignCredential(peer2CredentialMessage);
            //ASAPCertificate peer2IssuedPeer1Certificate = peer2PKI.acceptAndSignCredential(peer1CredentialMessage);

            //TODO: save certificates in model as they are likely needed again

            //peer1PKI.setSharkCredentialReceivedListener(this);

       //} catch (SharkException | IOException e) {
       //    ui.printError(e.getLocalizedMessage());
       // }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Exchanges the certificates between two peers.");
        return sb.toString();
    }

    @Override
    public void credentialReceived(CredentialMessage credentialMessage) {

    }
}
