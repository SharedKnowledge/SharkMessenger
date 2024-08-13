package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaireBuilder;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandKnownPeerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;

public class UICommandGetCertificatesBySubject extends UICommand {

    private final UICommandKnownPeerArgument subject;

    public UICommandGetCertificatesBySubject(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                             String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.subject = new UICommandKnownPeerArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("Subject peer name: ", this.subject)
                .build();
    }

    @Override
    public void execute() throws Exception {
        this.printTODOReimplement();
/*
        SharkPKIComponent pki = model.getPKIComponent();
        Collection<ASAPCertificate> certificates = pki.getCertificatesBySubject(this.subject.getValue().getUserID());

        certificates.forEach(certificate -> {
            StringBuilder sb = new StringBuilder();
            sb.append(certificate.getIssuerName());
            sb.append(" signed ");
            sb.append(certificate.getSubjectName());
            sb.append(" on ");
            sb.append(certificate.getValidSince());
            sb.append(System.lineSeparator());
            sb.append("The certificate is valid until: ");
            sb.append(certificate.getValidUntil());
            sb.append(System.lineSeparator());
            sb.append(System.lineSeparator());

            ui.printInfo(sb.toString());
        });

 */
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Returns all certificates by a specified subject.");
        return sb.toString();
    }

    /**
     * @param arguments in following order:
     * <ol>
     *  <li>subject - peerID</li>
     * </ol>
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if(arguments.size() < 1) {
            System.err.println("subject missing - name of the peer for whom that certificate was issued");
            return false;
        }


        boolean isParsable = subject.tryParse(arguments.get(0));
        if(!isParsable) {
            System.err.println("problems parsing subject - name of the peer for whom that certificate was issued");
        }
        return isParsable;
    }
}