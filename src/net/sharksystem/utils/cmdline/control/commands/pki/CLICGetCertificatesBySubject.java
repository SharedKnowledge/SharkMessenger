package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICSharkPeerArgument;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;

import java.util.Collection;

public class CLICGetCertificatesBySubject extends CLICommand {

    private final CLICSharkPeerArgument owner;

    private final CLICSharkPeerArgument subject;

    public CLICGetCertificatesBySubject(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.owner = new CLICSharkPeerArgument();
        this.subject = new CLICSharkPeerArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Owner peer name: ", this.owner).
                addQuestion("Subject peer name: ", this.subject).
                build();
    }

    @Override
    public void execute() throws Exception {
        SharkPKIComponent pki = model.getPKIFromPeer(this.owner.getValue());
        Collection<ASAPCertificate> certificates = pki.getCertificatesBySubject(this.subject.getValue().getPeerID());

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
        sb.append("Returns all certificates from a specific subject.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
