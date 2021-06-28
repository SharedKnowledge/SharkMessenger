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
    public static final String MESSAGE = "Hi";
    public static final byte[] MESSAGE_BYTE = MESSAGE.getBytes();
    public static final String MESSAGE_1 = "Hello";
    public static final byte[] MESSAGE_1_BYTE = MESSAGE_1.getBytes();
    public static final String MESSAGE_2 = "Hi, Alice";
    public static final byte[] MESSAGE_2_BYTE = MESSAGE_2.getBytes();
    public static final String MESSAGE_3 = "How are you?";
    public static final byte[] MESSAGE_3_BYTE = MESSAGE_3.getBytes();
    public static final String MESSAGE_4 = "Fine and you?";
    public static final byte[] MESSAGE_4_BYTE = MESSAGE_4.getBytes();

    public static final String URI = "sn2://all";

    private static int testNumber = 0;

    private static int portNumber = 5000;

    public static int getPortNumber() {
        return TestHelper.portNumber++;
    }


    //////////////// member
    public final String subRootFolder;
    public final String aliceFolder;
    public final String bobFolder;
    public final String claraFolder;
    private final String davidFolder;

    /* apologize. That's 1990er code... no getter but protected member */
    protected SharkTestPeerFS alicePeer;
    protected SharkTestPeerFS bobPeer;
    protected SharkTestPeerFS claraPeer;
    protected SharkTestPeerFS davidPeer;

    protected SharkMessengerComponent aliceMessenger;
    protected SharkMessengerComponent bobMessenger;
    protected SharkMessengerComponent claraMessenger;
    protected SharkMessengerComponent davidMessenger;

    protected SharkMessengerComponentImpl aliceMessengerImpl;
    protected SharkMessengerComponentImpl bobMessengerImpl;
    protected SharkMessengerComponentImpl claraMessengerImpl;
    protected SharkMessengerComponentImpl davidMessengerImpl;

    private final String testName;

    public TestHelper(String testName) {
        this.testName = testName;

        this.subRootFolder = TestConstants.ROOT_DIRECTORY + testName + "/";

        this.aliceFolder = subRootFolder + ALICE_ID;
        this.bobFolder = subRootFolder + BOB_ID;
        this.claraFolder = subRootFolder + CLARA_ID;
        this.davidFolder = subRootFolder + DAVID_ID;
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

    /*
     * Scenario 2:
     * a) Alice and Bob exchanged credential messages and provided certificates for each other
     * b) Bob and Clara exchanged credential messages and provided certificates for each other
     * c) Alice and Clara exchanged credential messages and provided certificates for each other
     * d) Alice and David exchanged credential messages and provided certificates for each other
     * e) Bob and David exchanged credential messages and provided certificates for each other
     * f) Clara and David exchanged credential messages and provided certificates for each other
     */
    public void setUpScenario_2() throws SharkException, ASAPSecurityException, IOException {
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

        String davidFolderName = davidFolder + "_" + testNumber;
        SharkTestPeerFS.removeFolder(davidFolderName);
        this.davidPeer = new SharkTestPeerFS(DAVID_ID, davidFolderName);
        TestHelper.setupComponent(this.davidPeer);

        testNumber++;

        // start peers
        this.alicePeer.start();
        this.bobPeer.start();
        this.claraPeer.start();
        this.davidPeer.start();

        // add some keys as described in scenario settings
        SharkPKIComponent alicePKI = (SharkPKIComponent) this.alicePeer.getComponent(SharkPKIComponent.class);
        SharkPKIComponent bobPKI = (SharkPKIComponent) this.bobPeer.getComponent(SharkPKIComponent.class);
        SharkPKIComponent claraPKI = (SharkPKIComponent) this.claraPeer.getComponent(SharkPKIComponent.class);
        SharkPKIComponent davidPKI = (SharkPKIComponent) this.davidPeer.getComponent(SharkPKIComponent.class);

        // create credential messages
        CredentialMessage aliceCredentialMessage = alicePKI.createCredentialMessage();
        CredentialMessage bobCredentialMessage = bobPKI.createCredentialMessage();
        CredentialMessage claraCredentialMessage = claraPKI.createCredentialMessage();
        CredentialMessage davidCredentialMessage = davidPKI.createCredentialMessage();

        // a) Alice and Bob exchange and accept credential messages and issue certificates
        alicePKI.acceptAndSignCredential(bobCredentialMessage);
        bobPKI.acceptAndSignCredential(aliceCredentialMessage);

        // b) Bob and Clara meet, accept credential messages and issue certificates
        claraPKI.acceptAndSignCredential(bobCredentialMessage);
        bobPKI.acceptAndSignCredential(claraCredentialMessage);

        // c) Alice and Clara meet, accept credential messages and issue certificates
        alicePKI.acceptAndSignCredential(claraCredentialMessage);
        claraPKI.acceptAndSignCredential(aliceCredentialMessage);

        // d) Alice and David meet, accept credential messages and issue certificates
        alicePKI.acceptAndSignCredential(davidCredentialMessage);
        davidPKI.acceptAndSignCredential(aliceCredentialMessage);

        // e) Bob and David meet, accept credential messages and issue certificates
        bobPKI.acceptAndSignCredential(davidCredentialMessage);
        davidPKI.acceptAndSignCredential(bobCredentialMessage);

        // f) Clara and David meet, accept credential messages and issue certificates
        claraPKI.acceptAndSignCredential(davidCredentialMessage);
        davidPKI.acceptAndSignCredential(claraCredentialMessage);

        ///////////// check stability of SharkPKI - just in case - it is a copy of a test in this project:
        // check identity assurance
        int iaAliceSideBob = alicePKI.getIdentityAssurance(BOB_ID);
        int iaAliceSideClara = alicePKI.getIdentityAssurance(CLARA_ID);
        int iaAliceSideDavid = alicePKI.getIdentityAssurance(DAVID_ID);

        int iaBobSideAlice = bobPKI.getIdentityAssurance(ALICE_ID);
        int iaBobSideClara = bobPKI.getIdentityAssurance(CLARA_ID);
        int iaBobSideDavid = bobPKI.getIdentityAssurance(DAVID_ID);

        int iaClaraSideAlice = claraPKI.getIdentityAssurance(ALICE_ID);
        int iaClaraSideBob = claraPKI.getIdentityAssurance(BOB_ID);
        int iaClaraSideDavid = claraPKI.getIdentityAssurance(DAVID_ID);

        int iaDavidSideAlice = davidPKI.getIdentityAssurance(ALICE_ID);
        int iaDavidSideBob = davidPKI.getIdentityAssurance(BOB_ID);
        int iaDavidSideClara = davidPKI.getIdentityAssurance(CLARA_ID);

        Assert.assertEquals(10, iaAliceSideBob); // met
        System.out.println("10 - okay, Alice met Bob");
        Assert.assertEquals(10, iaAliceSideClara); // met
        System.out.println("0 - okay, Alice met Clara");
        Assert.assertEquals(10, iaAliceSideDavid); // met
        System.out.println("0 - okay, Alice met David");
        Assert.assertEquals(10, iaBobSideAlice); // met
        System.out.println("10 - okay, Bob met Alice");
        Assert.assertEquals(10, iaBobSideClara); // met
        System.out.println("10 - okay, Bob met Clara");
        Assert.assertEquals(10, iaBobSideDavid); // met
        System.out.println("10 - okay, Bob met David");
        Assert.assertEquals(10, iaClaraSideAlice); // met
        System.out.println("5 - okay, Clara met Alice");
        Assert.assertEquals(10, iaClaraSideBob); // met
        System.out.println("10 - okay, Clara met Bob");
        Assert.assertEquals(10, iaClaraSideDavid); // met
        System.out.println("10 - okay, Clara met David");
        Assert.assertEquals(10, iaDavidSideAlice); // met
        System.out.println("5 - okay, David met Alice");
        Assert.assertEquals(10, iaDavidSideBob); // met
        System.out.println("10 - okay, David met Bob");
        Assert.assertEquals(10, iaDavidSideClara); // met
        System.out.println("10 - okay, David met David");

        System.out.println("********************************************************************");
        System.out.println("**                          PKI works                             **");
        System.out.println("********************************************************************");
        /////////////////////// PKI works

        this.aliceMessenger = (SharkMessengerComponent) this.alicePeer.getComponent(SharkMessengerComponent.class);
        this.bobMessenger = (SharkMessengerComponent) this.bobPeer.getComponent(SharkMessengerComponent.class);
        this.claraMessenger = (SharkMessengerComponent) this.claraPeer.getComponent(SharkMessengerComponent.class);
        this.davidMessenger = (SharkMessengerComponent) this.davidPeer.getComponent(SharkMessengerComponent.class);

        // set up backdoors
        this.aliceMessengerImpl = (SharkMessengerComponentImpl) this.aliceMessenger;
        this.bobMessengerImpl = (SharkMessengerComponentImpl) this.bobMessenger;
        this.claraMessengerImpl = (SharkMessengerComponentImpl) this.claraMessenger;
        this.davidMessengerImpl = (SharkMessengerComponentImpl) this.davidMessenger;
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
