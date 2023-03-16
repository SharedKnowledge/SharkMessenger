package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICKnownPeerArgument;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;

import java.util.Collection;

public class CLICGetCertificatesByIssuer extends CLICommand {

    private final CLICKnownPeerArgument issuer;
    public CLICGetCertificatesByIssuer(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.issuer = new CLICKnownPeerArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder()
                .addQuestion("Issuer peer name: ", this.issuer)
                .build();
    }

    @Override
    public void execute() throws Exception {
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
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Returns all certificates from a specific issuer.");
        return sb.toString();
    }

}
