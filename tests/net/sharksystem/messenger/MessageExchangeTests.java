package net.sharksystem.messenger;

import net.sharksystem.*;
import net.sharksystem.asap.ASAPChannel;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.ASAPStorage;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Version 1 - Scenarios (open channel communication (open routing) only)  selective routing is Version 2
 *
 * Tests:
 * i) Alice sends unsigned / unencrypted messages to B and C. They are received
 * ii) Alice sends signed / unencrypted messages to B and C. They are received and verified. A and B show
 * likelihood authentic senders
 * iii) Clara sends signed / unencrypted messages to B and A. B can verify, A can not. A and B show
 * likelihood authentic senders - which is unknown on A side
 * iv) Alice sends one unsigned and (with Bob public key encrypted) message to B and C. B can encrypt. C cannot.
 * v) Clara sends two messages (unsigned and encrypted) to B and than A. Both can decrypt. B is assure of
 * Clara identity, Alice has no clue.
 * vi) Alice sends two signed and encrypted messages to C and B with success.
 * vii) Alice sends signed B. B encounters C. B can verify, C can not. Like ii) but routed over B.
 */

public class MessageExchangeTests extends TestHelper {

    public MessageExchangeTests() {
        super(MessageExchangeTests.class.getSimpleName());
    }

    public void runEncounter(SharkTestPeerFS leftPeer, SharkTestPeerFS rightPeer, boolean stop)
            throws SharkException, IOException, InterruptedException {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("                       start encounter: "
                + leftPeer.getASAPPeer().getPeerID() + " <--> " + rightPeer.getASAPPeer().getPeerID());
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        leftPeer.getASAPTestPeerFS().startEncounter(TestHelper.getPortNumber(), rightPeer.getASAPTestPeerFS());
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
    public void test1_1() throws SharkException, ASAPException, IOException, InterruptedException {
        this.setUpScenario_1();
        this.runTest_1();
    }

    public void runTest_1() throws SharkException, IOException, InterruptedException, ASAPException {
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

        SharkMessengerChannel bobChannel = bobMessenger.getChannel(URI);
        SharkMessage sharkMessage = bobChannel.getMessages().getSharkMessage(0, true);
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
    public void test1_2() throws SharkException, ASAPSecurityException, IOException, InterruptedException {
        this.setUpScenario_1();
        this.runTest_2();
    }

    public void runTest_2() throws SharkException, IOException, InterruptedException, ASAPSecurityException {
        // Alice broadcast message in channel URI - signed, not encrypted
        aliceMessenger.sendSharkMessage(MESSAGE_BYTE, URI, true, false);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // test results
        SharkMessengerChannel bobChannel = bobMessenger.getChannel(URI);
        SharkMessage sharkMessage = bobChannel.getMessages().getSharkMessage(0, true);
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
        SharkMessengerChannel claraChannel = claraMessenger.getChannel(URI);
        sharkMessage = claraChannel.getMessages().getSharkMessage(0, true);
        // message received by Bob from Alice?
        Assert.assertTrue(alicePeer.samePeer(sharkMessage.getSender()));
        Assert.assertTrue(Utils.compareArrays(sharkMessage.getContent(), MESSAGE_BYTE));
        Assert.assertFalse(sharkMessage.encrypted());
        Assert.assertTrue(sharkMessage.verified());

        SharkPKIComponent claraPKI = claraMessenger.getSharkPKI();
        CharSequence alicePeerID = alicePeer.getPeerID();
        int claraIdentityAssuranceOfIfAlice = claraPKI.getIdentityAssurance(alicePeerID);
        // got certificate from Bob - signing failure rate of Bob not changed - default is 50%.
        Assert.assertEquals(5, claraIdentityAssuranceOfIfAlice);
    }

    @Test
    public void test1_3() throws SharkException, ASAPSecurityException, IOException, InterruptedException {
        this.setUpScenario_1();
        // Clara broadcast message in channel URI - signed, not encrypted
        claraMessenger.sendSharkMessage(MESSAGE_BYTE, URI, true, false);

        ///////////////////////////////// Encounter Clara - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.claraPeer, this.bobPeer, true);

        // test results
        SharkMessengerChannel bobChannel = bobMessenger.getChannel(URI);
        SharkMessage sharkMessage = bobChannel.getMessages().getSharkMessage(0, true);
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
        SharkMessengerChannel aliceChannel = aliceMessenger.getChannel(URI);
        sharkMessage = aliceChannel.getMessages().getSharkMessage(0, true);
        // message received by Bob from Alice?
        Assert.assertTrue(claraPeer.samePeer(sharkMessage.getSender()));
        Assert.assertTrue(Utils.compareArrays(sharkMessage.getContent(), MESSAGE_BYTE));
        Assert.assertFalse(sharkMessage.encrypted());
        Assert.assertFalse(sharkMessage.verified());

        SharkPKIComponent claraPKI = claraMessenger.getSharkPKI();
        int claraIdentityAssuranceOfIfAlice = claraPKI.getIdentityAssurance(alicePeer.getPeerID());
        // Alice never met Clara nor has she got a certificate
        Assert.assertEquals(5, claraIdentityAssuranceOfIfAlice);

        SharkPKIComponent alicePKI = aliceMessenger.getSharkPKI();
        int aliceIdentityAssuranceOfIfClara = alicePKI.getIdentityAssurance(claraPeer.getPeerID());
        // Alice never met Clara nor has she got a certificate
        Assert.assertEquals(0, aliceIdentityAssuranceOfIfClara);
    }

    @Test
    public void test1_4() throws SharkException, ASAPSecurityException, IOException, InterruptedException {
        this.setUpScenario_1();

        // Alice broadcast message in channel URI - signed, not encrypted
        aliceMessenger.sendSharkMessage(MESSAGE_BYTE, URI, bobPeer.getPeerID(), false, true);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // test results
        SharkMessengerChannel bobChannel = bobMessenger.getChannel(URI);
        SharkMessage sharkMessage = bobChannel.getMessages().getSharkMessage(0, true);
        Assert.assertTrue(sharkMessage.couldBeDecrypted());
        // message received by Bob from Alice?
        Assert.assertTrue(alicePeer.samePeer(sharkMessage.getSender()));
        Assert.assertTrue(Utils.compareArrays(sharkMessage.getContent(), MESSAGE_BYTE));
        Assert.assertTrue(sharkMessage.encrypted());
        Assert.assertFalse(sharkMessage.verified());

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // test results
        SharkMessengerChannel claraChannel = claraMessenger.getChannel(URI);
        sharkMessage = claraChannel.getMessages().getSharkMessage(0, true);
        // message received by Bob from Alice?
        Assert.assertFalse(sharkMessage.couldBeDecrypted());
    }

    /*
    * v) Clara sends two messages (unsigned and encrypted) to B and than A. Both can decrypt.
    * Bob is sure of Clara's identity. Alice is not.
     */

    private void oneEncryptableOneIsNot(SharkMessageList msgList) throws SharkMessengerException {
        SharkMessage sharkMessage0 = msgList.getSharkMessage(0, true);
        SharkMessage sharkMessage1 = msgList.getSharkMessage(1, true);

        SharkMessage decryptedMsg = sharkMessage0.couldBeDecrypted() ? sharkMessage0 : sharkMessage1;
        SharkMessage undecryptedMsg = sharkMessage0.couldBeDecrypted() ? sharkMessage1 :  sharkMessage0;

        // the other cannot be decrypted - it was not meant for this receiver
        Assert.assertFalse(undecryptedMsg.couldBeDecrypted());
    }

    @Test
    public void test1_5() throws SharkException, ASAPSecurityException, IOException, InterruptedException {
        this.setUpScenario_1();

        // send two encrypted message - one for Alice, another for Bob.
        claraMessenger.sendSharkMessage(MESSAGE_BYTE, URI, alicePeer.getPeerID(), false, true);
        claraMessenger.sendSharkMessage(MESSAGE_BYTE, URI, bobPeer.getPeerID(), false, true);

        ///////////////////////////////// Encounter Clara - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.claraPeer, this.bobPeer, true);

        // test results
        SharkMessengerChannel bobChannel = bobMessenger.getChannel(URI);
        SharkMessageList bobChannelMessages = bobChannel.getMessages();
        Assert.assertEquals(2, bobChannelMessages.size());
        this.oneEncryptableOneIsNot(bobChannelMessages);

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // test results
        SharkMessengerChannel aliceChannel = aliceMessenger.getChannel(URI);
        // variant has a better performance - if order is not of any concern
        SharkMessageList aliceChannelMessages = aliceChannel.getMessages(false, false);
        Assert.assertEquals(2, aliceChannelMessages.size());
        this.oneEncryptableOneIsNot(aliceChannelMessages);
    }
}
