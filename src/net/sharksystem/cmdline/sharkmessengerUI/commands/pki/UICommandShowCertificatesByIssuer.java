package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.pki.PKIHelper;
import net.sharksystem.pki.SharkPKIComponent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class UICommandShowCertificatesByIssuer extends UICommand {
    private final UICommandStringArgument issuer;

    public UICommandShowCertificatesByIssuer(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                             String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        this.issuer = new UICommandStringArgument(sharkMessengerApp);
    }

    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.size() < 1) {
            System.err.println("issuer name required");
            return false;
        }

        boolean isParsable = this.issuer.tryParse(arguments.get(0));

        if(!isParsable) {
            System.err.println("failed to parse issuer name" + arguments.get(0));
        }

        return isParsable;
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }

    @Override
    protected void execute() throws Exception {
        SharkPKIComponent sharkPKIComponent = this.getSharkMessengerApp().getSharkPKIComponent();
        String issuerName = this.issuer.getValue();
        Collection<ASAPCertificate> certificatesByIssuer = sharkPKIComponent.getCertificatesByIssuer(issuerName);

        for(ASAPCertificate certificate : certificatesByIssuer) {
            System.out.println(PKIHelper.asapCert2String(certificate));
            System.out.println("\n");
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("show all certificates issued by a specific peer.");
        // append hint for how to use
        return sb.toString();
    }
}