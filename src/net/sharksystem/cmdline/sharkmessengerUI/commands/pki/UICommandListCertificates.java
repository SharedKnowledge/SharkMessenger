package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandNoParameter;
import net.sharksystem.pki.SharkPKIComponent;

import java.util.Collection;
import java.util.Set;

public class UICommandListCertificates extends AbstractCommandNoParameter {
    public UICommandListCertificates(SharkMessengerApp sharkMessengerApp, SharkMessengerUI smUI, String lsCerts, boolean b) {
        super(sharkMessengerApp, smUI, lsCerts, b);
    }

    @Override
    protected void execute() throws Exception {
        SharkPKIComponent pki = this.getSharkMessengerApp().getSharkPKIComponent();
        Set<ASAPCertificate> certificates = pki.getCertificates();

        StringBuilder sb = new StringBuilder();
        if(certificates == null || certificates.isEmpty()) {
            sb.append("no certificates");
        } else {
            sb.append(certificates.size());
            sb.append(" certificate(s): \n");
            int i = 1;
            boolean first = true;
            for(ASAPCertificate certificate : certificates) {
                if(first) first = false;
                else sb.append("\n");
                sb.append(i++);
                sb.append(": \n");
                sb.append(new PKIPrinter(pki).getCertificateAsString(certificate));
            }
            sb.append("\n");
            this.getSharkMessengerApp().tellUI(sb.toString());
        }
    }

    @Override
    public String getDescription() {
        return "list all certificates";
    }
}
