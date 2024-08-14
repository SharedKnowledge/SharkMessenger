package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import net.sharksystem.SharkException;
import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.pki.PKIHelper;

import java.util.Collection;
import java.util.List;

public abstract class AbstractCommandProduceCertificateListByPeerID extends UICommand {
    private final UICommandStringArgument peer;
    private String peerID = null;

    public AbstractCommandProduceCertificateListByPeerID(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                                         String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        this.peer = new UICommandStringArgument(sharkMessengerApp);
    }

    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.size() < 1) {
            this.getSharkMessengerApp().tellUI("no peer name provided - use local peer ("
                    + this.getSharkMessengerApp().getPeerName() + ")");
            try {
                this.peerID = this.getSharkMessengerApp().getSharkPeer().getPeerID().toString();
                return true;
            } catch (SharkException e) {
                this.getSharkMessengerApp().tellUIError("fatal: cannot get peer id: " + e);
                return false;
            }
        } else {
            boolean isParsable = this.peer.tryParse(arguments.get(0));
            if (!isParsable) {
                System.err.println("failed to parse issuer id" + arguments.get(0));
            }
            return isParsable;
        }

    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }

    protected void ensurePeerIDSet() {

    }

    protected void tellUICertificates(Collection<ASAPCertificate> certificateCollection) {
        if(certificateCollection == null | certificateCollection.isEmpty()) {
            this.getSharkMessengerApp().tellUI("not matching certificates found");
        }
        for (ASAPCertificate certificate : certificateCollection) {
            StringBuilder sb = new StringBuilder();
            sb.append(PKIHelper.asapCert2String(certificate));
            sb.append("\n");
            this.getSharkMessengerApp().tellUI(sb.toString());
        }
    }

    @Override
    protected void execute() throws Exception {
        // ensure peer ID is present
        if (this.peerID == null) {
            this.peerID = this.peer.getValue();
        }

        // print results
        this.tellUICertificates(this.produceCertificateCollection(this.peerID));
    }

    protected abstract Collection<ASAPCertificate> produceCertificateCollection(String peerID) throws ASAPSecurityException;
}