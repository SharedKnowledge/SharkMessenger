package net.sharksystem.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.app.messenger.SharkNetMessengerComponent;
import net.sharksystem.app.messenger.SharkNetMessengerComponentFactory;
import net.sharksystem.app.messenger.SharkNetMessengerComponentImpl;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.pki.*;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

import static net.sharksystem.messenger.TestConstants.*;

public class TestHelper {
    private static int testNumber = 0;
    private static int portNumber = 5000;
    public static int getPortNumber() {
        return TestHelper.portNumber++;
    }

    public final String subRootFolder;
    public final String aliceFolder;
    public final String bobFolder;
    public final String claraFolder;
    private final String davidFolder;

    protected SharkTestPeerFS alicePeer;
    protected SharkTestPeerFS bobPeer;
    protected SharkTestPeerFS claraPeer;
    protected SharkTestPeerFS davidPeer;

    protected SharkNetMessengerComponent aliceMessenger;
    protected SharkNetMessengerComponent bobMessenger;
    protected SharkNetMessengerComponent claraMessenger;
    protected SharkNetMessengerComponent davidMessenger;

    protected SharkNetMessengerComponentImpl aliceMessengerImpl;
    protected SharkNetMessengerComponentImpl bobMessengerImpl;
    protected SharkNetMessengerComponentImpl claraMessengerImpl;
    protected SharkNetMessengerComponentImpl davidMessengerImpl;

    public TestHelper(String testName) {
        this.subRootFolder = TestConstants.ROOT_DIRECTORY + testName + "/";

        this.aliceFolder = this.subRootFolder + ALICE_ID;
        this.bobFolder = this.subRootFolder + BOB_ID;
        this.claraFolder = this.subRootFolder + CLARA_ID;
        this.davidFolder = this.subRootFolder + DAVID_ID;
    }

    /**
     * Scenario 1:<br/>
     * a) Alice and Bob exchanged credential messages and provided certificates for each other<br/>
     * b) Bob and Clara exchanged credential messages and provided certificates for each other<br/>
     * b) Clara received certificate issued by Bob for subject Alice<br/>
     * c) Alice has no information about Clara
     */
    public void setUpScenario_1() throws SharkException, IOException {
        System.out.println("test number == " + TestHelper.testNumber);
        String aliceFolderName = aliceFolder + "_" + TestHelper.testNumber;
        SharkTestPeerFS.removeFolder(aliceFolderName);
        this.alicePeer = new SharkTestPeerFS(ALICE_NAME, aliceFolderName);
        TestHelper.addComponentsToSharkPeer(ALICE_ID, this.alicePeer);

        String bobFolderName = bobFolder + "_" + TestHelper.testNumber;
        SharkTestPeerFS.removeFolder(bobFolderName);
        this.bobPeer = new SharkTestPeerFS(BOB_NAME, bobFolderName);
        TestHelper.addComponentsToSharkPeer(BOB_ID, this.bobPeer);

        String claraFolderName = claraFolder + "_" + TestHelper.testNumber;
        SharkTestPeerFS.removeFolder(claraFolderName);
        this.claraPeer = new SharkTestPeerFS(CLARA_NAME, claraFolderName);
        TestHelper.addComponentsToSharkPeer(CLARA_ID, this.claraPeer);

        TestHelper.testNumber++;

        // start peers
        this.alicePeer.start(ALICE_ID);
        this.bobPeer.start(BOB_ID);
        this.claraPeer.start(CLARA_ID);

        // add some keys as described in scenario settings
        SharkPKIComponent alicePKI = (SharkPKIComponent) this.alicePeer.getComponent(SharkPKIComponent.class);
        SharkPKIComponent bobPKI = (SharkPKIComponent) this.bobPeer.getComponent(SharkPKIComponent.class);
        SharkPKIComponent claraPKI = (SharkPKIComponent) this.claraPeer.getComponent(SharkPKIComponent.class);

        // create credential messages
        CredentialMessage aliceCredentialMessage = ((SharkPKIDebugSupport) alicePKI).createCredentialMessage();
        CredentialMessage bobCredentialMessage = ((SharkPKIDebugSupport) bobPKI).createCredentialMessage();
        CredentialMessage claraCredentialMessage = ((SharkPKIDebugSupport) claraPKI).createCredentialMessage();

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

        Assertions.assertEquals(10, iaAliceSideBob);
        System.out.println("10 - okay, Alice met Bob");
        Assertions.assertEquals(0, iaAliceSideClara);
        System.out.println("0 - okay, Alice knows nothing about Clara");
        Assertions.assertEquals(10, iaBobSideAlice);
        System.out.println("10 - okay, Bob met Alice");
        Assertions.assertEquals(10, iaBobSideClara);
        System.out.println("10 - okay, Bob met Clara");
        Assertions.assertEquals(5, iaClaraSideAlice);
        System.out.println("5 - okay, Clara has got a certificate issued by Bob (with failure rate 5)");
        Assertions.assertEquals(10, iaClaraSideBob);
        System.out.println("10 - okay, Clara met Bob");

        System.out.println("********************************************************************");
        System.out.println("**                          PKI works                             **");
        System.out.println("********************************************************************");

        this.aliceMessenger = (SharkNetMessengerComponent) this.alicePeer.getComponent(SharkNetMessengerComponent.class);
        this.bobMessenger = (SharkNetMessengerComponent) this.bobPeer.getComponent(SharkNetMessengerComponent.class);
        this.claraMessenger = (SharkNetMessengerComponent) this.claraPeer.getComponent(SharkNetMessengerComponent.class);

        // set up backdoors
        this.aliceMessengerImpl = (SharkNetMessengerComponentImpl) this.aliceMessenger;
        this.bobMessengerImpl = (SharkNetMessengerComponentImpl) this.bobMessenger;
        this.claraMessengerImpl = (SharkNetMessengerComponentImpl) this.claraMessenger;
    }

    /**
     * Scenario 2: Everyone meets<br/>
     * a) Alice and Bob exchanged credential messages and provided certificates for each other<br/>
     * b) Bob and Clara exchanged credential messages and provided certificates for each other<br/>
     * c) Alice and Clara exchanged credential messages and provided certificates for each other<br/>
     * d) Alice and David exchanged credential messages and provided certificates for each other<br/>
     * e) Bob and David exchanged credential messages and provided certificates for each other<br/>
     * f) Clara and David exchanged credential messages and provided certificates for each other
     */
    public void setUpScenario_2() throws SharkException, IOException {
        System.out.println("test number == " + TestHelper.testNumber);
        String aliceFolderName = this.aliceFolder + "_" + TestHelper.testNumber;
        SharkTestPeerFS.removeFolder(aliceFolderName);
        this.alicePeer = new SharkTestPeerFS(ALICE_NAME, aliceFolderName);
        TestHelper.addComponentsToSharkPeer(ALICE_ID, this.alicePeer);

        String bobFolderName = this.bobFolder + "_" + TestHelper.testNumber;
        SharkTestPeerFS.removeFolder(bobFolderName);
        this.bobPeer = new SharkTestPeerFS(BOB_NAME, bobFolderName);
        TestHelper.addComponentsToSharkPeer(BOB_ID, this.bobPeer);

        String claraFolderName = this.claraFolder + "_" + TestHelper.testNumber;
        SharkTestPeerFS.removeFolder(claraFolderName);
        this.claraPeer = new SharkTestPeerFS(CLARA_NAME, claraFolderName);
        TestHelper.addComponentsToSharkPeer(CLARA_ID, this.claraPeer);

        String davidFolderName = this.davidFolder + "_" + TestHelper.testNumber;
        SharkTestPeerFS.removeFolder(davidFolderName);
        this.davidPeer = new SharkTestPeerFS(DAVID_NAME, davidFolderName);
        TestHelper.addComponentsToSharkPeer(DAVID_ID, this.davidPeer);

        TestHelper.testNumber++;

        // start peers
        this.alicePeer.start(ALICE_ID);
        this.bobPeer.start(BOB_ID);
        this.claraPeer.start(CLARA_ID);
        this.davidPeer.start(DAVID_ID);

        // add some keys as described in scenario settings
        SharkPKIComponent alicePKI = (SharkPKIComponent) this.alicePeer.getComponent(SharkPKIComponent.class);
        SharkPKIComponent bobPKI = (SharkPKIComponent) this.bobPeer.getComponent(SharkPKIComponent.class);
        SharkPKIComponent claraPKI = (SharkPKIComponent) this.claraPeer.getComponent(SharkPKIComponent.class);
        SharkPKIComponent davidPKI = (SharkPKIComponent) this.davidPeer.getComponent(SharkPKIComponent.class);

        // create credential messages
        CredentialMessage aliceCredentialMessage = ((SharkPKIDebugSupport) alicePKI).createCredentialMessage();
        CredentialMessage bobCredentialMessage = ((SharkPKIDebugSupport) bobPKI).createCredentialMessage();
        CredentialMessage claraCredentialMessage = ((SharkPKIDebugSupport) claraPKI).createCredentialMessage();
        CredentialMessage davidCredentialMessage = ((SharkPKIDebugSupport) davidPKI).createCredentialMessage();

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

        Assertions.assertEquals(10, iaAliceSideBob);
        System.out.println("10 - okay, Alice met Bob");
        Assertions.assertEquals(10, iaAliceSideClara);
        System.out.println("10 - okay, Alice met Clara");
        Assertions.assertEquals(10, iaAliceSideDavid);
        System.out.println("10 - okay, Alice met David");
        Assertions.assertEquals(10, iaBobSideAlice);
        System.out.println("10 - okay, Bob met Alice");
        Assertions.assertEquals(10, iaBobSideClara);
        System.out.println("10 - okay, Bob met Clara");
        Assertions.assertEquals(10, iaBobSideDavid);
        System.out.println("10 - okay, Bob met David");
        Assertions.assertEquals(10, iaClaraSideAlice);
        System.out.println("10 - okay, Clara met Alice");
        Assertions.assertEquals(10, iaClaraSideBob);
        System.out.println("10 - okay, Clara met Bob");
        Assertions.assertEquals(10, iaClaraSideDavid);
        System.out.println("10 - okay, Clara met David");
        Assertions.assertEquals(10, iaDavidSideAlice);
        System.out.println("10 - okay, David met Alice");
        Assertions.assertEquals(10, iaDavidSideBob);
        System.out.println("10 - okay, David met Bob");
        Assertions.assertEquals(10, iaDavidSideClara);
        System.out.println("10 - okay, David met David");

        System.out.println("********************************************************************");
        System.out.println("**                          PKI works                             **");
        System.out.println("********************************************************************");

        this.aliceMessenger = (SharkNetMessengerComponent) this.alicePeer.getComponent(SharkNetMessengerComponent.class);
        this.bobMessenger = (SharkNetMessengerComponent) this.bobPeer.getComponent(SharkNetMessengerComponent.class);
        this.claraMessenger = (SharkNetMessengerComponent) this.claraPeer.getComponent(SharkNetMessengerComponent.class);
        this.davidMessenger = (SharkNetMessengerComponent) this.davidPeer.getComponent(SharkNetMessengerComponent.class);

        // set up backdoors
        this.aliceMessengerImpl = (SharkNetMessengerComponentImpl) this.aliceMessenger;
        this.bobMessengerImpl = (SharkNetMessengerComponentImpl) this.bobMessenger;
        this.claraMessengerImpl = (SharkNetMessengerComponentImpl) this.claraMessenger;
        this.davidMessengerImpl = (SharkNetMessengerComponentImpl) this.davidMessenger;
    }

    /**
     * Adds a SharkPKIComponent and a SharkMessengerComponent to a given SharkPeer
     */
    public static void addComponentsToSharkPeer(CharSequence peerID, SharkPeer sharkPeer)
            throws SharkException {

        // create a component factory
        SharkPKIComponentFactory certificateComponentFactory =
                new SharkPKIComponentFactory();

        // register this component with shark peer - note: we use interface SharkPeer
        sharkPeer.addComponent(certificateComponentFactory, SharkPKIComponent.class);

        SharkNetMessengerComponentFactory messengerFactory =
                new SharkNetMessengerComponentFactory(
                        (SharkPKIComponent) sharkPeer.getComponent(SharkPKIComponent.class)
                );

        sharkPeer.addComponent(messengerFactory, SharkNetMessengerComponent.class);
    }
}
