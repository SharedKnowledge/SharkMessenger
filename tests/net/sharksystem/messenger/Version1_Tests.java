package net.sharksystem.messenger;

import net.sharksystem.*;
import net.sharksystem.asap.ASAPChannel;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.ASAPStorage;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.asap.pki.CredentialMessageInMemo;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import net.sharksystem.utils.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static net.sharksystem.messenger.TestConstants.*;

/**
 * Version 1 - Scenarios (open channel communication (open routing) only)  selective routing is Version 2
 *
 * Scenario 1:
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
 * vi) Alice sends signed and encrypted messages to C and B with success.
 * vii) Alice sends signed B. B encounters C. B can verify, C can not. Like ii) but routed over B.
 * viii) A peer sets filter to get messages from channel: Filter [signed/verified only y|n], [decryptable y|n]
 *
 */


public class Version1_Tests {
    public static final String SUB_ROOT_DIRECTORY = TestConstants.ROOT_DIRECTORY
            + Version1_Tests.class.getSimpleName() + "/";
    public static final String MESSAGE = "Hi";
    public static final byte[] MESSAGE_BYTE = MESSAGE.getBytes();
    public static final String URI = "sn2://all";

    public static final String ALICE_FOLDER = SUB_ROOT_DIRECTORY + ALICE_ID;
    public static final String BOB_FOLDER = SUB_ROOT_DIRECTORY + BOB_ID;
    public static final String CLARA_FOLDER = SUB_ROOT_DIRECTORY + CLARA_ID;
    private SharkTestPeerFS alicePeer;
    private SharkTestPeerFS bobPeer;
    private SharkTestPeerFS claraPeer;

    private static int portNumber = 5000;
    private SharkMessengerComponent aliceMessenger;
    private SharkMessengerComponent bobMessenger;
    private SharkMessengerComponent claraMessenger;

    private SharkMessengerComponentImpl aliceMessengerImpl;
    private SharkMessengerComponentImpl bobMessengerImpl;
    private SharkMessengerComponentImpl claraMessengerImpl;

    private int getPortNumber() {
        return Version1_Tests.portNumber++;
    }

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

    public void setUpScenario_1() throws SharkException, ASAPSecurityException, IOException {
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

        this.aliceMessenger = (SharkMessengerComponent) this.alicePeer.getComponent(SharkMessengerComponent.class);
        this.bobMessenger = (SharkMessengerComponent) this.bobPeer.getComponent(SharkMessengerComponent.class);
        this.claraMessenger = (SharkMessengerComponent) this.claraPeer.getComponent(SharkMessengerComponent.class);

        // set up backdoors
        this.aliceMessengerImpl = (SharkMessengerComponentImpl) this.aliceMessenger;
        this.bobMessengerImpl = (SharkMessengerComponentImpl) this.bobMessenger;
        this.claraMessengerImpl = (SharkMessengerComponentImpl) this.claraMessenger;
    }

    public void runEncounter(SharkTestPeerFS leftPeer, SharkTestPeerFS rightPeer, boolean stop)
            throws SharkException, IOException, InterruptedException {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("                       start encounter: "
                + leftPeer.getASAPPeer().getPeerID() + " <--> " + rightPeer.getASAPPeer().getPeerID());
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        leftPeer.getASAPTestPeerFS().startEncounter(this.getPortNumber(), rightPeer.getASAPTestPeerFS());
        // give them moment to exchange data
        Thread.sleep(1000);
        //Thread.sleep(Long.MAX_VALUE);
        System.out.println("slept a moment");

        if(stop) {
            System.out.println(">>>>>>>>>>>>>>>>>  stop encounter: "
                    + leftPeer.getASAPPeer().getPeerID() + " <--> " + rightPeer.getASAPPeer().getPeerID());
            leftPeer.getASAPTestPeerFS().stopEncounter(this.claraPeer.getASAPTestPeerFS());
            Thread.sleep(100);
        }
    }

    @Test
    public void test1_i() throws SharkException, ASAPException, IOException, InterruptedException {
        this.setUpScenario_1();
        this.runTest_i();
    }

    public void runTest_i() throws SharkException, IOException, InterruptedException, ASAPException {
        // Alice broadcast message in channel URI - not signed, not encrypted
        aliceMessenger.sendSharkMessage(MESSAGE_BYTE, URI, false, false);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // test results
        ASAPStorage bobAsapStorage = bobMessengerImpl.getASAPStorage();
        List<CharSequence> senderList = bobAsapStorage.getSender();
        Assert.assertNotNull(senderList);
        Assert.assertFalse(senderList.isEmpty());
        CharSequence senderID = senderList.get(0);
        Assert.assertTrue(alicePeer.samePeer(senderID));
        ASAPStorage senderIncomingStorage = bobAsapStorage.getExistingIncomingStorage(senderID);
        ASAPChannel channel = senderIncomingStorage.getChannel(URI);
        byte[] message = channel.getMessages().getMessage(0, true);
        Assert.assertNotNull(message);

        SharkMessage sharkMessage = bobMessenger.getSharkMessage(URI, 0, true);
        // message received by Bob from Alice?
        Assert.assertTrue(alicePeer.samePeer(sharkMessage.getSender()));
        Assert.assertTrue(Utils.compareArrays(sharkMessage.getContent(), MESSAGE_BYTE));
        Assert.assertFalse(sharkMessage.encrypted());
        Assert.assertFalse(sharkMessage.verified());

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // test results
        // message received by Clara from Alice?
        Assert.assertTrue(alicePeer.samePeer(sharkMessage.getSender()));
        Assert.assertTrue(Utils.compareArrays(sharkMessage.getContent(), MESSAGE_BYTE));
        Assert.assertFalse(sharkMessage.encrypted());
        Assert.assertFalse(sharkMessage.verified());
    }

    @Test
    public void test1_ii() throws SharkException, ASAPSecurityException, IOException, InterruptedException {
        this.setUpScenario_1();
        this.runTest_ii();
    }

    public void runTest_ii() throws SharkException, IOException, InterruptedException, ASAPSecurityException {
        // Alice broadcast message in channel URI - signed, not encrypted
        aliceMessenger.sendSharkMessage(MESSAGE_BYTE, URI, true, false);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // test results
        SharkMessage sharkMessage = bobMessenger.getSharkMessage(URI, 0, true);
        // message received by Bob from Alice?
        Assert.assertTrue(alicePeer.samePeer(sharkMessage.getSender()));
        Assert.assertTrue(Utils.compareArrays(sharkMessage.getContent(), MESSAGE_BYTE));
        Assert.assertFalse(sharkMessage.encrypted());
        Assert.assertTrue(sharkMessage.verified());

        SharkPKIComponent bobPKI = bobMessenger.getSharkPKI();
        int bobIdentityAssuranceOfIfAlice = bobPKI.getIdentityAssurance(alicePeer.getPeerID());
        Assert.assertEquals(10, bobIdentityAssuranceOfIfAlice); // both met

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // test results
        sharkMessage = claraMessenger.getSharkMessage(URI, 0, true);
        // message received by Bob from Alice?
        Assert.assertTrue(alicePeer.samePeer(sharkMessage.getSender()));
        Assert.assertTrue(Utils.compareArrays(sharkMessage.getContent(), MESSAGE_BYTE));
        Assert.assertFalse(sharkMessage.encrypted());
        Assert.assertTrue(sharkMessage.verified());

        SharkPKIComponent claraPKI = bobMessenger.getSharkPKI();
        int claraIdentityAssuranceOfIfAlice = claraPKI.getIdentityAssurance(alicePeer.getPeerID());
        // got certificate from Bob - signing failure rate of Bob not changed - default is 50%.
        // TODO... activate this failure again!
        //Assert.assertEquals(5, claraIdentityAssuranceOfIfAlice);
    }

    @Test
    public void test1_iii() throws SharkException, ASAPSecurityException, IOException, InterruptedException {
        this.setUpScenario_1();
        // Alice broadcast message in channel URI - signed, not encrypted
        claraMessenger.sendSharkMessage(MESSAGE_BYTE, URI, true, false);

        ///////////////////////////////// Encounter Clara - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.claraPeer, this.bobPeer, true);

        // test results
        SharkMessage sharkMessage = bobMessenger.getSharkMessage(URI, 0, true);
        // message received by Bob from Alice?
        Assert.assertTrue(claraPeer.samePeer(sharkMessage.getSender()));
        Assert.assertTrue(Utils.compareArrays(sharkMessage.getContent(), MESSAGE_BYTE));
        Assert.assertFalse(sharkMessage.encrypted());
        Assert.assertTrue(sharkMessage.verified());

        SharkPKIComponent bobPKI = bobMessenger.getSharkPKI();
        int bobIdentityAssuranceOfIfAlice = bobPKI.getIdentityAssurance(claraPeer.getPeerID());
        Assert.assertEquals(10, bobIdentityAssuranceOfIfAlice); // both met

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // test results
        sharkMessage = aliceMessenger.getSharkMessage(URI, 0, true);
        // message received by Bob from Alice?
        Assert.assertTrue(claraPeer.samePeer(sharkMessage.getSender()));
        Assert.assertTrue(Utils.compareArrays(sharkMessage.getContent(), MESSAGE_BYTE));
        Assert.assertFalse(sharkMessage.encrypted());
        Assert.assertFalse(sharkMessage.verified());

        SharkPKIComponent claraPKI = aliceMessenger.getSharkPKI();
        int claraIdentityAssuranceOfIfAlice = claraPKI.getIdentityAssurance(alicePeer.getPeerID());
        // Alice never met Clara nor has she got a certificate
        // TODO... activate this failure again!
        //Assert.assertEquals(0, claraIdentityAssuranceOfIfAlice);
    }

    @Test
    public void test1_iv() throws SharkException, ASAPSecurityException, IOException, InterruptedException {
        this.setUpScenario_1();

        // Alice broadcast message in channel URI - signed, not encrypted
        aliceMessenger.sendSharkMessage(MESSAGE_BYTE, URI, bobPeer.getPeerID(), false, true);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // test results
        SharkMessage sharkMessage = bobMessenger.getSharkMessage(URI, 0, true);
        // message received by Bob from Alice?
        Assert.assertTrue(alicePeer.samePeer(sharkMessage.getSender()));
        Assert.assertTrue(Utils.compareArrays(sharkMessage.getContent(), MESSAGE_BYTE));
        Assert.assertTrue(sharkMessage.encrypted());
        Assert.assertFalse(sharkMessage.verified());

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // test results
        sharkMessage = claraMessenger.getSharkMessage(URI, 0, true);
        // message received by Bob from Alice?
        Assert.assertTrue(alicePeer.samePeer(sharkMessage.getSender()));
        Assert.assertTrue(Utils.compareArrays(sharkMessage.getContent(), MESSAGE_BYTE));
        Assert.assertTrue(sharkMessage.encrypted());
        Assert.assertFalse(sharkMessage.verified());
    }
}
