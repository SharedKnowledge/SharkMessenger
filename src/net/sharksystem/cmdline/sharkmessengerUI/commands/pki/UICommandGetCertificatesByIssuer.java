package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaireBuilder;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandKnownPeerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;

public class UICommandGetCertificatesByIssuer extends UICommand {

    private final UICommandKnownPeerArgument issuer;
    public UICommandGetCertificatesByIssuer(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                            String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.issuer = new UICommandKnownPeerArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("Issuer peer name: ", this.issuer)
                .build();
    }

    @Override
    public void execute() throws Exception {
        this.printTODOReimplement();
/*
        SharkPKIComponent pki = model.getPKIComponent();
        Collection<ASAPCertificate> certificates = pki.getCertificatesByIssuer(this.issuer.getValue().getUserID());

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
        sb.append("Returns all certificates from a specific issuer.");
        return sb.toString();
    }

    /**
     * @param arguments in following order:
     * <ol>
     *  <li>issuer - peerID</li>
     * </ol>
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if(arguments.size() < 1) {
            return false;
        }
        
        boolean isParsable = issuer.tryParse(arguments.get(0));
        return isParsable;
    }
}