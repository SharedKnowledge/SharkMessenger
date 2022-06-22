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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * v) Clara sends two messages (unsigned and encrypted) to B and A afterwards. Both can decrypt. B is assured of
 * Clara identity, Alice has no clue.
 * vi) Alice sends two signed and encrypted messages to C and B with success.
 * vii) Alice sends signed B. B encounters C. B can verify, C can not. Like ii) but routed over B.
 * Viii) Sorted message handling between Alice and Bob without timestamp. Alice send message bob receive,
 * and send another one, and then send a replyTo message
 * iX) Sorted message handling between Alice, Bob, Clara and Tina without timestamp. Alice send message bob receive,
 *  * and send another one, and then send a replyTo message
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

    @Test
    public void test1_8() throws ASAPSecurityException, SharkException, IOException, InterruptedException {
        this.setUpScenario_1();

        SortedMessageFactory aliceSortedMessageFactory = new SortedSharkMessageFactory();
        SortedMessageFactory bobSortedMessageFactory = new SortedSharkMessageFactory();
        SortedMessage aliceSortedMessage = aliceSortedMessageFactory.produceSortedMessage(MESSAGE_BYTE, null);
        // Alice send a message to Bob
        aliceMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(aliceSortedMessage) , URI, bobPeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // Test results Bob received message
        SharkMessengerChannel bobChannel = bobMessenger.getChannel(URI);
        SharkMessage bobSharkMessage = bobChannel.getMessages().getSharkMessage(0, true);
        Assert.assertTrue(bobSharkMessage.couldBeDecrypted());
        Assert.assertTrue(bobSharkMessage.encrypted());
        Assert.assertTrue(bobSharkMessage.verified());
        // message received by Bob from Alice?
        Assert.assertTrue(alicePeer.samePeer(bobSharkMessage.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage bobSortedMessage = SortedMessageImpl.byteArrayToSortedMessage(bobSharkMessage.getContent());
        // Add the received message to the factory
        bobSortedMessageFactory.addIncomingSortedMessage(bobSortedMessage);
        // Check sortedMessage id
        Assert.assertEquals(aliceSortedMessage.getID(), bobSortedMessage.getID());

        Assert.assertTrue(Utils.compareArrays(bobSortedMessage.getContent(), MESSAGE_BYTE));

        // SortedMessage parents should be []
        Assert.assertTrue(bobSortedMessage.getParents().size() == 0);

        // Bob send a message to Alice
        SortedMessage bobSortedMessage_1 = bobSortedMessageFactory.produceSortedMessage(MESSAGE_1_BYTE, null);
        bobMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(bobSortedMessage_1) , URI, alicePeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Bob - Alice ////////////////////////////////////////////////////
        this.runEncounter(this.bobPeer, this.alicePeer, true);

        // Test results Bob received message
        SharkMessengerChannel aliceChannel = aliceMessenger.getChannel(URI);
        SharkMessage aliceSharkMessage_1 = aliceChannel.getMessages().getSharkMessage(0, true);
        Assert.assertTrue(aliceSharkMessage_1.couldBeDecrypted());
        Assert.assertTrue(aliceSharkMessage_1.encrypted());
        Assert.assertTrue(aliceSharkMessage_1.verified());
        // message received by Alice from Bob?
        Assert.assertTrue(bobPeer.samePeer(aliceSharkMessage_1.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage aliceSortedMessage_1 = SortedMessageImpl.byteArrayToSortedMessage(aliceSharkMessage_1.getContent());
        // Add the received message to the factory
        aliceSortedMessageFactory.addIncomingSortedMessage(bobSortedMessage_1);
        // Check sortedMessage id
        Assert.assertEquals(bobSortedMessage_1.getID(), aliceSortedMessage_1.getID());

        Assert.assertTrue(Utils.compareArrays(aliceSortedMessage_1.getContent(), MESSAGE_1_BYTE));

        // SortedMessage parents should be ["id"] bobSortedMessage.getID()
        Assert.assertTrue(aliceSortedMessage_1.getParents().size() == 1);
        Assert.assertTrue(aliceSortedMessage_1.getParents().contains(bobSortedMessage.getID()));

        // Bob send a message to Alice with replyTo relation to bobSortedMessage_1
        SortedMessage bobSortedMessage_2 = bobSortedMessageFactory.produceSortedMessage(MESSAGE_2_BYTE, bobSortedMessage.getID());
        bobMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(bobSortedMessage_2) , URI, alicePeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Bob - Alice ////////////////////////////////////////////////////
        this.runEncounter(this.bobPeer, this.alicePeer, true);

        // Test results Bob received message
        SharkMessage aliceSharkMessage_2 = aliceChannel.getMessages().getSharkMessage(1, true);
        Assert.assertTrue(aliceSharkMessage_2.couldBeDecrypted());
        Assert.assertTrue(aliceSharkMessage_2.encrypted());
        Assert.assertTrue(aliceSharkMessage_2.verified());
        // message received by Alice from Bob?
        Assert.assertTrue(bobPeer.samePeer(aliceSharkMessage_2.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage aliceSortedMessage_2 = SortedMessageImpl.byteArrayToSortedMessage(aliceSharkMessage_2.getContent());
        // Add the received message to the factory
        aliceSortedMessageFactory.addIncomingSortedMessage(aliceSortedMessage_2);
        // Check sortedMessage id
        Assert.assertEquals(bobSortedMessage_2.getID(), aliceSortedMessage_2.getID());

        Assert.assertTrue(Utils.compareArrays(aliceSortedMessage_2.getContent(), MESSAGE_2_BYTE));

        // SortedMessage parents should be ["id"] bobSortedMessage_1.getID()
        Assert.assertTrue(bobSortedMessage_2.getParents().size() == 1);
        Assert.assertTrue(bobSortedMessage_2.getParents().contains(aliceSortedMessage_1.getID()));

        // SortedMessage reply To should be bobSortedMessage.getID()
        Assert.assertEquals(bobSortedMessage_2.getReplyTo(), bobSortedMessage.getID());
    }

    @Test
    public void test1_9() throws ASAPSecurityException, SharkException, IOException, InterruptedException {
        this.setUpScenario_2();

        SortedMessageFactory aliceSortedMessageFactory = new SortedSharkMessageFactory();
        SortedMessageFactory bobSortedMessageFactory = new SortedSharkMessageFactory();
        SortedMessageFactory claraSortedMessageFactory = new SortedSharkMessageFactory();
        SortedMessageFactory davidSortedMessageFactory = new SortedSharkMessageFactory();

        // Alice send a message to Bob
        SortedMessage aliceSortedMessage = aliceSortedMessageFactory.produceSortedMessage(MESSAGE_BYTE, null);
        aliceMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(aliceSortedMessage) , URI, bobPeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // Test results Bob received message
        SharkMessengerChannel bobChannel = bobMessenger.getChannel(URI);
        SharkMessage bobSharkMessage = bobChannel.getMessages().getSharkMessage(0, true);
        Assert.assertTrue(bobSharkMessage.couldBeDecrypted());
        Assert.assertTrue(bobSharkMessage.encrypted());
        Assert.assertTrue(bobSharkMessage.verified());
        // message received by Bob from Alice?
        Assert.assertTrue(alicePeer.samePeer(bobSharkMessage.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage bobSortedMessage = SortedMessageImpl.byteArrayToSortedMessage(bobSharkMessage.getContent());
        // Add the received message to the factory
        bobSortedMessageFactory.addIncomingSortedMessage(bobSortedMessage);
        // Check sortedMessage id
        Assert.assertEquals(aliceSortedMessage.getID(), bobSortedMessage.getID());

        Assert.assertTrue(Utils.compareArrays(bobSortedMessage.getContent(), MESSAGE_BYTE));

        // SortedMessage parents should be []
        Assert.assertEquals(0, bobSortedMessage.getParents().size());

        // Alice send a message to Clara
        aliceMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(aliceSortedMessage) , URI, claraPeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // Test results clara received message
        SharkMessengerChannel claraChannel = bobMessenger.getChannel(URI);
        SharkMessage claraSharkMessage = claraChannel.getMessages().getSharkMessage(0, true);
        Assert.assertTrue(claraSharkMessage.couldBeDecrypted());
        Assert.assertTrue(claraSharkMessage.encrypted());
        Assert.assertTrue(claraSharkMessage.verified());
        // message received by Bob from Alice?
        Assert.assertTrue(alicePeer.samePeer(claraSharkMessage.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage claraSortedMessage = SortedMessageImpl.byteArrayToSortedMessage(claraSharkMessage.getContent());
        // Add the received message to the factory
        claraSortedMessageFactory.addIncomingSortedMessage(claraSortedMessage);
        // Check sortedMessage id
        Assert.assertEquals(aliceSortedMessage.getID(), claraSortedMessage.getID());

        Assert.assertTrue(Utils.compareArrays(claraSortedMessage.getContent(), MESSAGE_BYTE));

        // SortedMessage parents should be []
        Assert.assertEquals(0, claraSortedMessage.getParents().size());

        // Bob send message to Alice
        SortedMessage bobSortedMessage_1 = bobSortedMessageFactory.produceSortedMessage(MESSAGE_1_BYTE, aliceSortedMessage.getID());
        bobMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(bobSortedMessage_1) , URI, alicePeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Bob - Alice ////////////////////////////////////////////////////
        this.runEncounter(this.bobPeer, this.alicePeer, true);

        // Test results Alice received message
        SharkMessengerChannel aliceChannel = aliceMessenger.getChannel(URI);
        SharkMessage aliceSharkMessage_1 = aliceChannel.getMessages().getSharkMessage(0, true);
        Assert.assertTrue(aliceSharkMessage_1.couldBeDecrypted());
        Assert.assertTrue(aliceSharkMessage_1.encrypted());
        Assert.assertTrue(aliceSharkMessage_1.verified());
        // message received by Alice from Bob?
        Assert.assertTrue(bobPeer.samePeer(aliceSharkMessage_1.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage aliceSortedMessage_1 = SortedMessageImpl.byteArrayToSortedMessage(aliceSharkMessage_1.getContent());
        // Add the received message to the factory
        aliceSortedMessageFactory.addIncomingSortedMessage(aliceSortedMessage_1);
        // Check sortedMessage id
        Assert.assertEquals(bobSortedMessage_1.getID(), aliceSortedMessage_1.getID());

        Assert.assertTrue(Utils.compareArrays(aliceSortedMessage_1.getContent(), MESSAGE_1_BYTE));

        // SortedMessage parents should be ["id"] bobSortedMessage.getID()
        Assert.assertEquals(1, aliceSortedMessage_1.getParents().size());
        Assert.assertTrue(aliceSortedMessage_1.getParents().contains(bobSortedMessage.getID()));

        // Clara send message to Alice
        SortedMessage claraSortedMessage_1 = claraSortedMessageFactory.produceSortedMessage(MESSAGE_BYTE, aliceSortedMessage.getID());
        claraMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(claraSortedMessage_1) , URI, alicePeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Clara - Alice ////////////////////////////////////////////////////
        this.runEncounter(this.claraPeer, this.alicePeer, true);

        // Test results Alice received message
        SharkMessage aliceSharkMessage_11 = aliceChannel.getMessages().getSharkMessage(0, true);
        Assert.assertTrue(aliceSharkMessage_11.couldBeDecrypted());
        Assert.assertTrue(aliceSharkMessage_11.encrypted());
        Assert.assertTrue(aliceSharkMessage_11.verified());
        // message received by Alice from Bob?
        Assert.assertTrue(claraPeer.samePeer(aliceSharkMessage_11.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage aliceSortedMessage_11 = SortedMessageImpl.byteArrayToSortedMessage(aliceSharkMessage_11.getContent());
        // Add the received message to the factory
        aliceSortedMessageFactory.addIncomingSortedMessage(aliceSortedMessage_11);
        // Check sortedMessage id
        Assert.assertEquals(claraSortedMessage_1.getID(), aliceSortedMessage_11.getID());

        Assert.assertTrue(Utils.compareArrays(aliceSortedMessage_11.getContent(), MESSAGE_BYTE));

        // SortedMessage parents should be ["id"] bobSortedMessage.getID()
        Assert.assertEquals(1, aliceSortedMessage_11.getParents().size());
        Assert.assertTrue(aliceSortedMessage_11.getParents().contains(claraSortedMessage.getID()));

        // Alice send a response to Bob
        SortedMessage aliceSortedMessage_3 = aliceSortedMessageFactory.produceSortedMessage(MESSAGE_3_BYTE, null);
        aliceMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(aliceSortedMessage_3) , URI, bobPeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // Test results Bob received message
        SharkMessage bobSharkMessage_3 = bobChannel.getMessages().getSharkMessage(3, true);
        Assert.assertTrue(bobSharkMessage_3.couldBeDecrypted());
        Assert.assertTrue(bobSharkMessage_3.encrypted());
        Assert.assertTrue(bobSharkMessage_3.verified());
        // message received by Bob from Alice?
        Assert.assertTrue(alicePeer.samePeer(bobSharkMessage_3.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage bobSortedMessage_3 = SortedMessageImpl.byteArrayToSortedMessage(bobSharkMessage_3.getContent());
        // Add the received message to the factory
        bobSortedMessageFactory.addIncomingSortedMessage(bobSortedMessage_3);
        // Check sortedMessage id
        Assert.assertEquals(aliceSortedMessage_3.getID(), bobSortedMessage_3.getID());

        Assert.assertTrue(Utils.compareArrays(bobSortedMessage_3.getContent(), MESSAGE_3_BYTE));
        // SortedMessage parents should be ["id1", "id2"] aliceSortedMessage_1.getID(), aliceSortedMessage_11.getID()
        Set<CharSequence> aliceSortedMessage_3_parents = new HashSet<>();
        aliceSortedMessage_3_parents.add(aliceSortedMessage_1.getID());
        aliceSortedMessage_3_parents.add(aliceSortedMessage_11.getID());

        Assert.assertEquals(2, bobSortedMessage_3.getParents().size());
        Assert.assertTrue(bobSortedMessage_3.getParents().containsAll(aliceSortedMessage_3_parents));

        // Alice Send a response to Clara
        aliceMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(aliceSortedMessage_3) , URI, claraPeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // Test results Clara received message
        SharkMessage claraSharkMessage_3 = claraChannel.getMessages().getSharkMessage(3, true);
        Assert.assertTrue(claraSharkMessage_3.couldBeDecrypted());
        Assert.assertTrue(claraSharkMessage_3.encrypted());
        Assert.assertTrue(claraSharkMessage_3.verified());
        // message received by Bob from Alice?
        Assert.assertTrue(alicePeer.samePeer(claraSharkMessage_3.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage claraSortedMessage_3 = SortedMessageImpl.byteArrayToSortedMessage(claraSharkMessage_3.getContent());
        // Add the received message to the factory
        bobSortedMessageFactory.addIncomingSortedMessage(claraSortedMessage_3);
        // Check sortedMessage id
        Assert.assertEquals(aliceSortedMessage_3.getID(), claraSortedMessage_3.getID());

        Assert.assertTrue(Utils.compareArrays(claraSortedMessage_3.getContent(), MESSAGE_3_BYTE));
        // SortedMessage parents should be ["id1", "id2"] aliceSortedMessage_1.getID(), aliceSortedMessage_11.getID()
        Assert.assertEquals(2, claraSortedMessage_3.getParents().size());
        Assert.assertTrue(claraSortedMessage_3.getParents().containsAll(aliceSortedMessage_3_parents));

        // David came later in the chat and got only the last message from Alice
        // David answer to Alice
        // David send a response to Alice
        SortedMessage davidSortedMessage_4 = aliceSortedMessageFactory.produceSortedMessage(MESSAGE_4_BYTE, null);
        davidMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(davidSortedMessage_4) , URI, alicePeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter David - Alice ////////////////////////////////////////////////////
        this.runEncounter(this.davidPeer, this.alicePeer, true);

        // Test results Alice received message
        SharkMessage aliceSharkMessage_4 = aliceChannel.getMessages().getSharkMessage(0, true);
        Assert.assertTrue(aliceSharkMessage_4.couldBeDecrypted());
        Assert.assertTrue(aliceSharkMessage_4.encrypted());
        Assert.assertTrue(aliceSharkMessage_4.verified());
        // message received by Alice from David?
        Assert.assertTrue(davidPeer.samePeer(aliceSharkMessage_4.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage aliceSortedMessage_4 = SortedMessageImpl.byteArrayToSortedMessage(aliceSharkMessage_4.getContent());
        // Add the received message to the factory
        davidSortedMessageFactory.addIncomingSortedMessage(aliceSortedMessage_4);
        // Check sortedMessage id
        Assert.assertEquals(davidSortedMessage_4.getID(), aliceSortedMessage_4.getID());
        Assert.assertTrue(Utils.compareArrays(aliceSortedMessage_4.getContent(), MESSAGE_4_BYTE));
        // SortedMessage parents should be ["id"] aliceSortedMessage_3.getID()
        Assert.assertEquals(1, aliceSortedMessage_4.getParents().size());
        Assert.assertTrue(aliceSortedMessage_4.getParents().contains(aliceSortedMessage_3.getID()));
    }
}
