package net.sharksystem.messenger;

import net.sharksystem.*;
import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.asap.pki.CredentialMessageInMemo;
import net.sharksystem.pki.CredentialMessage;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import org.junit.Test;

import java.io.IOException;

import static net.sharksystem.messenger.TestConstants.*;

/**
 * Version 1 - Scenarios (open channel communication (open routing) only)  selective routing is Version 2
 *
 * a) Alice and Bob exchanged keys
 * b) Bob met Clara - both exchanged keys - Clara has a Certificate of Alice issued by Bob
 * c) Alice has no information about Clara
 *
 * Tests:
 * i) Alice sends unsigned / unencrypted messages to B and C. They are received
 * ii) Alice sends signed / unencrypted messages to B and C. They are received and verified. A and B show
 * likelihood authentic senders
 * iii) Clara sends signed / unencrypted messages to B and A. B can verify, A can not. A and B show
 * likelihood authentic senders - which is unknown on A side
 * iv) Alice sends unsigned / encrypted messages to B and C. B and C can decrypt.
 * v) Clara sends unsigned / encrypted messages to B and A. B can decrypt. A cannot.
 * vi) Alice sends signed and encrypted messages to A and B with success.
 * vii) A peer sets filter to get messages from channel: Filter [signed/verified only y|n], [decryptable y|n]
 *
 */


public class Version1_Tests {
    public static final String SUB_ROOT_DIRECTORY = TestConstants.ROOT_DIRECTORY
            + Version1_Tests.class.getSimpleName() + "/";
    public static final String MESSAGE = "Hi";
    public static final String URI = "sn2://all";

    public static final String ALICE_FOLDER = SUB_ROOT_DIRECTORY + ALICE_ID;
    public static final String BOB_FOLDER = SUB_ROOT_DIRECTORY + BOB_ID;
    public static final String CLARA_FOLDER = SUB_ROOT_DIRECTORY + CLARA_ID;
    private SharkTestPeerFS alicePeer;
    private SharkTestPeerFS bobPeer;
    private SharkTestPeerFS claraPeer;

    private SharkMessengerComponent setupComponent(SharkPeer sharkPeer)
            throws SharkException {

        // create a component factory
        SharkPKIComponentFactory certificateComponentFactory = new SharkPKIComponentFactory();

        // register this component with shark peer - note: we use interface SharkPeer
        sharkPeer.addComponent(certificateComponentFactory, SharkPKIComponent.class);

        SharkMessengerComponentFactory messengerFactory =
                new SharkMessengerComponentFactory(
                        (SharkPKIComponent) sharkPeer.getComponent(SharkPKIComponent.class)
                );

        sharkPeer.addComponent(messengerFactory, SharkMessengerComponent.class);

        return (SharkMessengerComponent) sharkPeer.getComponent(SharkMessengerComponent.class);
    }

    public void setUpScenario1() throws SharkException, ASAPSecurityException, IOException {
        SharkTestPeerFS.removeFolder(ALICE_FOLDER);
        this.alicePeer = new SharkTestPeerFS(ALICE_ID, ALICE_FOLDER);
        this.setupComponent(this.alicePeer);

        SharkTestPeerFS.removeFolder(BOB_FOLDER);
        this.bobPeer = new SharkTestPeerFS(BOB_ID, BOB_FOLDER);
        this.setupComponent(this.bobPeer);

        SharkTestPeerFS.removeFolder(CLARA_FOLDER);
        this.claraPeer = new SharkTestPeerFS(CLARA_ID, CLARA_FOLDER);
        this.setupComponent(this.claraPeer);

        // start peers
        this.alicePeer.start();
        this.bobPeer.start();
        this.claraPeer.start();

        // add some keys as described in scenario settings
        SharkPKIComponent alicePKI = (SharkPKIComponent) this.alicePeer.getComponent(SharkPKIComponent.class);
        SharkPKIComponent bobPKI = (SharkPKIComponent) this.bobPeer.getComponent(SharkPKIComponent.class);
        SharkPKIComponent claraPKI = (SharkPKIComponent) this.claraPeer.getComponent(SharkPKIComponent.class);

        // let Bob accept ALice credentials and create a certificate
        CredentialMessageInMemo aliceCredentialMessage =
                new CredentialMessageInMemo(ALICE_ID, ALICE_NAME, System.currentTimeMillis(), alicePKI.getPublicKey());

        ASAPCertificate bobIssuedAliceCertificate = bobPKI.acceptAndSignCredential(aliceCredentialMessage);

        // Alice accepts Bob Public Key
        CredentialMessageInMemo bobCredentialMessage =
                new CredentialMessageInMemo(BOB_ID, BOB_NAME, System.currentTimeMillis(), bobPKI.getPublicKey());
        alicePKI.acceptAndSignCredential(bobCredentialMessage);

        CredentialMessageInMemo claraCredentialMessage =
                new CredentialMessageInMemo(CLARA_ID, CLARA_NAME, System.currentTimeMillis(), claraPKI.getPublicKey());

        // Bob knows Clara
        bobPKI.acceptAndSignCredential(claraCredentialMessage);
        // Clara knowns Bob
        claraPKI.acceptAndSignCredential(bobCredentialMessage);

        // Clara knows Alice certificate issued by Bob
        claraPKI.addCertificate(bobIssuedAliceCertificate);
    }

    @Test
    public void usage1() throws SharkException, ASAPSecurityException, IOException {
        this.setUpScenario1();
    }
}
