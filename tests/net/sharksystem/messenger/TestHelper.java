package net.sharksystem.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.pki.CredentialMessage;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import org.junit.Assert;

import java.io.IOException;

import static net.sharksystem.messenger.TestConstants.*;
import static net.sharksystem.messenger.TestConstants.BOB_ID;

public class TestHelper {
    //////////////// statics
    private static int testNumber = 0;

    public static int getPortNumber() {
        return TestHelper.portNumber++;
    }

    private static int portNumber = 5000;

    //////////////// member
    public final String subRootFolder;
    public final String aliceFolder;
    public final String bobFolder;
    public final String claraFolder;

    /* apologize. That's 1990er code... no getter but protected member */
    protected SharkTestPeerFS alicePeer;
    protected SharkTestPeerFS bobPeer;
    protected SharkTestPeerFS claraPeer;

    protected SharkMessengerComponent aliceMessenger;
    protected SharkMessengerComponent bobMessenger;
    protected SharkMessengerComponent claraMessenger;

    protected SharkMessengerComponentImpl aliceMessengerImpl;
    protected SharkMessengerComponentImpl bobMessengerImpl;
    protected SharkMessengerComponentImpl claraMessengerImpl;

    private final String testName;

    public TestHelper(String testName) {
        this.testName = testName;

        this.subRootFolder = TestConstants.ROOT_DIRECTORY + testName + "/";

        this.aliceFolder = subRootFolder + ALICE_ID;
        this.bobFolder = subRootFolder + BOB_ID;
        this.claraFolder = subRootFolder + CLARA_ID;
    }
    /*
     * Scenario 1:
     * a) Alice and Bob exchanged credential messages and provided certificates for each other
     * b) Bob and Clara exchanged credential messages and provided certificates for each other
     * b) Clara received certificate issued by Bob for subject Alice.
     * c) Alice has no information about Clara
     *
    */
    public void setUpScenario_1() throws SharkException, ASAPSecurityException, IOException {
        System.out.println("test number == " + testNumber);
        String aliceFolderName = aliceFolder + "_" + testNumber;
        SharkTestPeerFS.removeFolder(aliceFolderName);
        this.alicePeer = new SharkTestPeerFS(ALICE_ID, aliceFolderName);
        TestHelper.setupComponent(this.alicePeer);

        String bobFolderName = bobFolder + "_" + testNumber;
        SharkTestPeerFS.removeFolder(bobFolderName);
        this.bobPeer = new SharkTestPeerFS(BOB_ID, bobFolderName);
        TestHelper.setupComponent(this.bobPeer);

        String claraFolderName = claraFolder + "_" + testNumber;
        SharkTestPeerFS.removeFolder(claraFolderName);
        this.claraPeer = new SharkTestPeerFS(CLARA_ID, claraFolderName);
        TestHelper.setupComponent(this.claraPeer);

        testNumber++;

        // start peers
        this.alicePeer.start();
        this.bobPeer.start();
        this.claraPeer.start();

        // add some keys as described in scenario settings
        SharkPKIComponent alicePKI = (SharkPKIComponent) this.alicePeer.getComponent(SharkPKIComponent.class);
        SharkPKIComponent bobPKI = (SharkPKIComponent) this.bobPeer.getComponent(SharkPKIComponent.class);
        SharkPKIComponent claraPKI = (SharkPKIComponent) this.claraPeer.getComponent(SharkPKIComponent.class);

        // create credential messages
        CredentialMessage aliceCredentialMessage = alicePKI.createCredentialMessage();
        CredentialMessage bobCredentialMessage = bobPKI.createCredentialMessage();
        CredentialMessage claraCredentialMessage = claraPKI.createCredentialMessage();

        // a) Alice and Bob exchange and accept credential messages and issue certificates
        ASAPCertificate aliceIssuedBobCert = alicePKI.acceptAndSignCredential(bobCredentialMessage);
        ASAPCertificate bobIssuedAliceCert = bobPKI.acceptAndSignCredential(aliceCredentialMessage);

        // b) Bob and Clara meet, accept credential messages and issue certificates
        ASAPCertificate claraIssuedBobCert = claraPKI.acceptAndSignCredential(bobCredentialMessage);
        ASAPCertificate bobIssuedClaraCert = bobPKI.acceptAndSignCredential(claraCredentialMessage);

        // c) Clara gets certificate issued by Bob issued for Alice
        claraPKI.addCertificate(bobIssuedAliceCert);

        ///////////// check stability of SharkPKI - just in case - it is a copy of a test in this project:
        // check identity assurance
        int iaAliceSideBob = alicePKI.getIdentityAssurance(BOB_ID);
        int iaAliceSideClara = alicePKI.getIdentityAssurance(CLARA_ID);

        int iaBobSideAlice = bobPKI.getIdentityAssurance(ALICE_ID);
        int iaBobSideClara = bobPKI.getIdentityAssurance(CLARA_ID);

        int iaClaraSideAlice = claraPKI.getIdentityAssurance(ALICE_ID);
        int iaClaraSideBob = claraPKI.getIdentityAssurance(BOB_ID);

        Assert.assertEquals(10, iaAliceSideBob); // met
        System.out.println("10 - okay, Alice met Bob");
        Assert.assertEquals(0, iaAliceSideClara); // never seen, no certificate on Alice side
        System.out.println("0 - okay, Alice knows nothing about Clara");
        Assert.assertEquals(10, iaBobSideAlice); // met
        System.out.println("10 - okay, Bob met Alice");
        Assert.assertEquals(10, iaBobSideClara); // met
        System.out.println("10 - okay, Bob met Clara");
        Assert.assertEquals(5, iaClaraSideAlice); // got certificate from Bob, with default failure rate == 5
        System.out.println("5 - okay, Clara has got a certificate issued by Bob (with failure rate 5)");
        Assert.assertEquals(10, iaClaraSideBob); // met
        System.out.println("10 - okay, Clara met Bob");

        System.out.println("********************************************************************");
        System.out.println("**                          PKI works                             **");
        System.out.println("********************************************************************");
        /////////////////////// PKI works

        this.aliceMessenger = (SharkMessengerComponent) this.alicePeer.getComponent(SharkMessengerComponent.class);
        this.bobMessenger = (SharkMessengerComponent) this.bobPeer.getComponent(SharkMessengerComponent.class);
        this.claraMessenger = (SharkMessengerComponent) this.claraPeer.getComponent(SharkMessengerComponent.class);

        // set up backdoors
        this.aliceMessengerImpl = (SharkMessengerComponentImpl) this.aliceMessenger;
        this.bobMessengerImpl = (SharkMessengerComponentImpl) this.bobMessenger;
        this.claraMessengerImpl = (SharkMessengerComponentImpl) this.claraMessenger;
    }

    public static SharkMessengerComponent setupComponent(SharkPeer sharkPeer)
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


}
