package net.sharksystem.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.SortedMessage;
import net.sharksystem.SortedMessageFactory;
import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.pki.SharkPKIComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static net.sharksystem.messenger.TestConstants.*;

/**
 * Version 1 - Scenarios (open channel communication (open routing) only)  selective routing is Version 2<br/><br/>
 *
 * Tests:<br/>
 * 1) Alice sends unsigned / unencrypted messages to B and C. They are received<br/>
 * 2) Alice sends signed / unencrypted messages to B and C. They are received and verified. A and B show
 * likelihood authentic senders<br/>
 * 3) Clara sends signed / unencrypted messages to B and A. B can verify, A can not. A and B show
 * likelihood authentic senders - which is unknown on A side<br/>
 * 4) Alice sends one unsigned and (with Bob public key encrypted) message to B and C. B can encrypt. C cannot.<br/>
 * 5) Clara sends two messages (unsigned and encrypted) to B and A afterwards. Both can decrypt. B is assured of
 * Clara identity, Alice has no clue.<br/>
 * 6) Alice sends two signed and encrypted messages to C and B with success.<br/>
 * 7) Alice sends signed B. B encounters C. B can verify, C can not. Like ii) but routed over B.<br/>
 * 8) Sorted message handling between Alice and Bob without timestamp. Alice send message bob receive,
 * and send another one, and then send a replyTo message<br/>
 * 9) Sorted message handling between Alice, Bob, Clara and Tina without timestamp. Alice send message Bob receive,
 * and send another one, and then send a replyTo message
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

        leftPeer.getASAPTestPeerFS().startEncounter(TestHelper.getPortNumberIncremented(), rightPeer.getASAPTestPeerFS());
        // give them moment to exchange data
        Thread.sleep(1000);
        System.out.println("slept a moment");

        if(stop) {
            System.out.println(">>>>>>>>>>>>>>>>>  stop encounter: "
                    + leftPeer.getASAPPeer().getPeerID() + " <--> " + rightPeer.getASAPPeer().getPeerID());
            leftPeer.getASAPTestPeerFS().stopEncounter(rightPeer.getASAPTestPeerFS());
            Thread.sleep(100);
        }
    }

    /**
     * Alice sends unsigned / unencrypted messages to B and C. They are received and verified.
     * A and B show likelihood authentic senders
     */
    @Test
    public void test1_1() throws SharkException, IOException, InterruptedException {
        this.setUpScenario_1();

        // Alice broadcasts message in channel URI - not signed, not encrypted
        this.aliceMessenger.sendSharkMessage(MESSAGE_BYTE, URI, false, false);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // Did Bob receive the message correctly from Alice?
        SharkMessengerChannel bobChannel = this.bobMessenger.getChannel(URI);
        Assertions.assertEquals(1, bobChannel.getMessages().size());
        SharkMessage sharkMessage = bobChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertTrue(this.alicePeer.samePeer(sharkMessage.getSender()));
        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());
        Assertions.assertFalse(sharkMessage.encrypted());
        Assertions.assertFalse(sharkMessage.verified());

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // Did Clara receive the message correctly from Alice?
        SharkMessengerChannel claraChannel = this.claraMessenger.getChannel(URI);
        Assertions.assertEquals(1, claraChannel.getMessages().size());
        sharkMessage = claraChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertTrue(this.alicePeer.samePeer(sharkMessage.getSender()));
        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());
        Assertions.assertFalse(sharkMessage.encrypted());
        Assertions.assertFalse(sharkMessage.verified());
    }

    /**
     * Alice sends signed / unencrypted messages to B and C. They are received and verified.
     * A and B show likelihood authentic senders
     */
    @Test
    public void test1_2() throws SharkException, IOException, InterruptedException {
        this.setUpScenario_1();

        // Alice broadcasts message in channel URI - signed, not encrypted
        this.aliceMessenger.sendSharkMessage(MESSAGE_BYTE, URI, true, false);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // Did Bob receive the message correctly from Alice?
        SharkMessengerChannel bobChannel = this.bobMessenger.getChannel(URI);
        Assertions.assertEquals(1, bobChannel.getMessages().size());
        SharkMessage sharkMessage = bobChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertTrue(this.alicePeer.samePeer(sharkMessage.getSender()));
        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());
        Assertions.assertFalse(sharkMessage.encrypted());
        Assertions.assertTrue(sharkMessage.verified());

        // make sure identity assurance is 10 --> both met directly
        SharkPKIComponent bobPKI = this.bobMessenger.getSharkPKI();
        int identityAssurance = bobPKI.getIdentityAssurance(this.alicePeer.getPeerID());
        Assertions.assertEquals(10, identityAssurance);

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // Did Clara receive the message correctly from Alice?
        SharkMessengerChannel claraChannel = this.claraMessenger.getChannel(URI);
        Assertions.assertEquals(1, claraChannel.getMessages().size());
        sharkMessage = claraChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertTrue(this.alicePeer.samePeer(sharkMessage.getSender()));
        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());
        Assertions.assertFalse(sharkMessage.encrypted());
        Assertions.assertTrue(sharkMessage.verified());

        SharkPKIComponent claraPKI = this.claraMessenger.getSharkPKI();
        identityAssurance = claraPKI.getIdentityAssurance(this.alicePeer.getPeerID());
        // got certificate from Bob - signing failure rate of Bob not changed - default is 50%.
        Assertions.assertEquals(5, identityAssurance);
    }

    /**
     * Clara sends signed / unencrypted messages to B and A. B can verify, A can not.
     * A and B show likelihood authentic senders - which is unknown on A side
     */
    @Test
    public void test1_3() throws SharkException, IOException, InterruptedException {
        this.setUpScenario_1();

        // Clara broadcast message in channel URI - signed, not encrypted
        this.claraMessenger.sendSharkMessage(MESSAGE_BYTE, URI, true, false);

        ///////////////////////////////// Encounter Clara - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.claraPeer, this.bobPeer, true);

        // message received by Bob from Clara?
        SharkMessengerChannel bobChannel = this.bobMessenger.getChannel(URI);
        SharkMessage sharkMessage = bobChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertTrue(this.claraPeer.samePeer(sharkMessage.getSender()));
        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());
        Assertions.assertFalse(sharkMessage.encrypted());
        Assertions.assertTrue(sharkMessage.verified());

        SharkPKIComponent bobPKI = this.bobMessenger.getSharkPKI();
        int identityAssurance = bobPKI.getIdentityAssurance(claraPeer.getPeerID());
        Assertions.assertEquals(10, identityAssurance); // both met

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // message received by Alice from Clara?
        SharkMessengerChannel aliceChannel = this.aliceMessenger.getChannel(URI);
        sharkMessage = aliceChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertTrue(this.claraPeer.samePeer(sharkMessage.getSender()));
        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());
        Assertions.assertFalse(sharkMessage.encrypted());
        // Alice does not have a certificate from Clara --> sender can not be verified
        Assertions.assertFalse(sharkMessage.verified());

        SharkPKIComponent claraPKI = this.claraMessenger.getSharkPKI();
        int claraIdentityAssuranceOfIfAlice = claraPKI.getIdentityAssurance(this.alicePeer.getPeerID());
        // Alice never met Clara nor has she got a certificate
        Assertions.assertEquals(5, claraIdentityAssuranceOfIfAlice);

        SharkPKIComponent alicePKI = this.aliceMessenger.getSharkPKI();
        int aliceIdentityAssuranceOfIfClara = alicePKI.getIdentityAssurance(this.claraPeer.getPeerID());
        // Alice never met Clara nor has she got a certificate
        Assertions.assertEquals(0, aliceIdentityAssuranceOfIfClara);
    }

    /**
     * Alice sends one unsigned and (with Bob public key encrypted) message to B and C. B can encrypt. C cannot.
     */
    @Test
    public void test1_4() throws SharkException, IOException, InterruptedException {
        this.setUpScenario_1();

        // Alice broadcast message in channel URI - signed, not encrypted
        this.aliceMessenger.sendSharkMessage(MESSAGE_BYTE, URI, bobPeer.getPeerID(), false, true);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // message received by Bob from Alice?
        SharkMessengerChannel bobChannel = this.bobMessenger.getChannel(URI);
        SharkMessage sharkMessage = bobChannel.getMessages().getSharkMessage(0, true);
        // Can Bob encrypt his message?
        Assertions.assertTrue(sharkMessage.couldBeDecrypted());
        Assertions.assertTrue(this.alicePeer.samePeer(sharkMessage.getSender()));
        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());
        Assertions.assertTrue(sharkMessage.encrypted());
        Assertions.assertFalse(sharkMessage.verified());

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // message received by Clara from Alice?
        SharkMessengerChannel claraChannel = this.claraMessenger.getChannel(URI);
        sharkMessage = claraChannel.getMessages().getSharkMessage(0, true);
        // Can Clara encrypt Bobs message?
        Assertions.assertFalse(sharkMessage.couldBeDecrypted());
        // TODO encrypted is not assigned on excryptedMessagePack, therefore returns false - see InMemoMessage constructor
        Assertions.assertTrue(sharkMessage.encrypted());
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::verified);
        Assertions.assertEquals(1, sharkMessage.getRecipients().size());
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getSender);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getCreationTime);
        Assertions.assertEquals(1, sharkMessage.getASAPHopsList().size());
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getContent);
    }

    /**
     * Checks if only one of the first two messages in msgList can be decrypted
     * @param msgList The message list should only contain 2 entries
     * @return position of decrypted message in msgList
     * @throws IllegalArgumentException if size of msgList is not 2
     */
    private int oneEncryptableOneIsNot(SharkMessageList msgList) throws SharkMessengerException, IOException {
        if (msgList.size() != 2)
            throw new IllegalArgumentException("Not exactly two messages were given");

        SharkMessage sharkMessage0 = msgList.getSharkMessage(0, true);
        SharkMessage sharkMessage1 = msgList.getSharkMessage(1, true);

        // check if the first message is decryptable
        int positionOfDecryptedMsg = sharkMessage0.couldBeDecrypted() ? 0 : 1;

        // if the first message was decryptable, make sure the second ca not
        if (positionOfDecryptedMsg == 0)
            Assertions.assertFalse(sharkMessage1.couldBeDecrypted());

        return positionOfDecryptedMsg;
    }

    /**
     * Clara sends two messages (unsigned and encrypted) to B and A afterwards. Both can decrypt.
     * B is assured of Clara identity, Alice has no clue.
     */
    @Test
    public void test1_5() throws SharkException, IOException, InterruptedException {
        this.setUpScenario_1();

        // send two encrypted message - one for Bob, another for Alice.
        this.claraMessenger.sendSharkMessage(MESSAGE_BYTE, URI, this.bobPeer.getPeerID(), false, true);
        this.claraMessenger.sendSharkMessage(MESSAGE_BYTE, URI, this.alicePeer.getPeerID(), false, true);

        ///////////////////////////////// Encounter Clara - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.claraPeer, this.bobPeer, true);

        // Did Bob get Clara's messages?
        SharkMessengerChannel bobChannel = this.bobMessenger.getChannel(URI);
        SharkMessageList bobChannelMessages = bobChannel.getMessages();
        Assertions.assertEquals(2, bobChannelMessages.size());
        int posOfBobMsg = this.oneEncryptableOneIsNot(bobChannelMessages);
        int posOfAliceMsg = 1 - posOfBobMsg;

        // make sure Bob can encrypt his message
        SharkMessage sharkMessage = bobChannelMessages.getSharkMessage(posOfBobMsg, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertTrue(this.claraPeer.samePeer(sharkMessage.getSender()));
        Assertions.assertTrue(sharkMessage.encrypted());
        Assertions.assertFalse(sharkMessage.verified());
        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());

        // make sure Bob cannot decipher any information about the encrypted (Alice's) message
        sharkMessage = bobChannelMessages.getSharkMessage(posOfAliceMsg, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertTrue(sharkMessage.encrypted()); // TODO message was encrypted --> therefore return true
//        Assertions.assertFalse(sharkMessage.verified()); // TODO but this is encrypted
//        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::verified);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getSender);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getRecipients);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getCreationTime);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getASAPHopsList);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getContent);

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // Did Alice get Clara's messages?
        SharkMessengerChannel aliceChannelChannel = this.aliceMessenger.getChannel(URI);
        SharkMessageList aliceChannelMessages = aliceChannelChannel.getMessages();
        Assertions.assertEquals(2, aliceChannelMessages.size());
        posOfAliceMsg = this.oneEncryptableOneIsNot(aliceChannelMessages);
        posOfBobMsg = 1 - posOfAliceMsg;

        // make sure Alice can encrypt her message
        sharkMessage = aliceChannelMessages.getSharkMessage(posOfAliceMsg, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertTrue(this.claraPeer.samePeer(sharkMessage.getSender()));
        Assertions.assertTrue(sharkMessage.encrypted());
        Assertions.assertFalse(sharkMessage.verified());
        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());

        // make sure Alice cannot decipher any information about the encrypted message
        sharkMessage = aliceChannelMessages.getSharkMessage(posOfBobMsg, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertTrue(sharkMessage.encrypted()); // TODO message was encrypted --> therefore return true
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::verified);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getSender);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getRecipients);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getCreationTime);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getASAPHopsList);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getContent);
    }

    /**
     * Alice sends two signed and encrypted messages to C and B with success.
     */
    @Test
    public void test1_6() throws SharkException, IOException, InterruptedException {
        this.setUpScenario_1();

        // TODO first encounter, then send message

        // Alice sends two encrypted and signed messages, one to Bob and one to Clara
        this.aliceMessenger.sendSharkMessage(MESSAGE_BYTE, URI, this.bobPeer.getPeerID(), true, true);
        this.aliceMessenger.sendSharkMessage(MESSAGE_BYTE, URI, this.claraPeer.getPeerID(), true, true); // TODO alice does not know of clara in scenario 1 therefore no public key to encrypt with

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // Did Bob get the messages?
        SharkMessengerChannel bobChannel = this.bobMessenger.getChannel(URI);
        SharkMessageList sharkMessageList = bobChannel.getMessages();
        Assertions.assertEquals(2, sharkMessageList.size());
        // make sure that Bob can only encrypt one message and remember which it is
        int posOfBobMsg = this.oneEncryptableOneIsNot(sharkMessageList);
        int posOfClaraMsg = 1 - posOfBobMsg;

        // make sure Bob can verify that the messages were sent by Alice, and make sure he can only encrypt one of them
        SharkMessage sharkMessage = sharkMessageList.getSharkMessage(posOfBobMsg, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertTrue(this.alicePeer.samePeer(sharkMessage.getSender()));
        Assertions.assertTrue(sharkMessage.encrypted());
        Assertions.assertTrue(sharkMessage.verified());
        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());

        // make sure Bob cannot decipher any information about the encrypted message
        sharkMessage = sharkMessageList.getSharkMessage(posOfClaraMsg, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertFalse(sharkMessage.encrypted());
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::verified);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getSender);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getRecipients);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getCreationTime);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getASAPHopsList);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getContent);

        // make sure identity assurance is 10 --> both met directly
        SharkPKIComponent pki = this.bobMessenger.getSharkPKI();
        int identityAssurance = pki.getIdentityAssurance(this.alicePeer.getPeerID());
        Assertions.assertEquals(10, identityAssurance);

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // Did Clara get the messages?
        SharkMessengerChannel claraChannel = this.claraMessenger.getChannel(URI);
        sharkMessageList = claraChannel.getMessages();
        Assertions.assertEquals(2, sharkMessageList.size());
        // make sure that Clara can only encrypt one message and remember which it is
        posOfClaraMsg = this.oneEncryptableOneIsNot(sharkMessageList);
        posOfBobMsg = 1 - posOfClaraMsg;

        // make sure Clara can not verify that the messages were sent by Alice (they did not meet directly at first),
        // and make sure she can only encrypt one of them
        sharkMessage = sharkMessageList.getSharkMessage(posOfClaraMsg, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertTrue(this.alicePeer.samePeer(sharkMessage.getSender()));
        Assertions.assertTrue(sharkMessage.encrypted());
        Assertions.assertTrue(sharkMessage.verified());
        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());

        // make sure Bob cannot decipher any information about the encrypted message
        sharkMessage = sharkMessageList.getSharkMessage(posOfBobMsg, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertTrue(sharkMessage.encrypted());
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::verified);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getSender);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getRecipients);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getCreationTime);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getASAPHopsList);
        Assertions.assertThrows(ASAPSecurityException.class, sharkMessage::getContent);

        // make sure identity assurance is 5 --> they met through Bob at first
        pki = this.claraMessenger.getSharkPKI();
        identityAssurance = pki.getIdentityAssurance(this.alicePeer.getPeerID());
        Assertions.assertEquals(5, identityAssurance);
    }

    /**
     * Alice sends signed B. B encounters C. B can verify, C can not. Like ii) but routed over B.
     */
    @Test
    public void test1_7() throws SharkException, IOException, InterruptedException {
        this.setUpScenario_1();

        // Alice broadcasts a signed but not encrypted message
        this.aliceMessenger.sendSharkMessage(MESSAGE_BYTE, URI, true, false);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // Did Bob get the message and can verify that it was sent by Alice?
        SharkMessengerChannel bobChannel = this.bobMessenger.getChannel(URI);
        Assertions.assertEquals(1, bobChannel.getMessages().size());
        SharkMessage sharkMessage = bobChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertTrue(this.alicePeer.samePeer(sharkMessage.getSender()));
        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());
        Assertions.assertFalse(sharkMessage.encrypted());
        Assertions.assertTrue(sharkMessage.verified());

        // make sure identity assurance is 10 --> both met directly
        SharkPKIComponent pki = this.bobMessenger.getSharkPKI();
        int identityAssurance = pki.getIdentityAssurance(this.alicePeer.getPeerID());
        Assertions.assertEquals(10, identityAssurance);

        ///////////////////////////////// Encounter Bob - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.bobPeer, this.claraPeer, true);

        // Did Clara get Alice' message and can NOT verify that it was sent by Alice?
        SharkMessengerChannel claraChannel = this.claraMessenger.getChannel(URI);
        Assertions.assertEquals(1, claraChannel.getMessages().size());
        sharkMessage = claraChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertNotNull(sharkMessage);
        Assertions.assertTrue(this.alicePeer.samePeer(sharkMessage.getSender()));
        Assertions.assertArrayEquals(MESSAGE_BYTE, sharkMessage.getContent());
        Assertions.assertFalse(sharkMessage.encrypted());
        Assertions.assertTrue(sharkMessage.verified());

        pki = this.claraMessenger.getSharkPKI();
        // make sure identity assurance is 10 --> Bob and Clara met directly
        identityAssurance = pki.getIdentityAssurance(this.bobPeer.getPeerID());
        Assertions.assertEquals(10, identityAssurance);
        // make sure identity assurance is 5 --> Clara met Alice through Bob
        identityAssurance = pki.getIdentityAssurance(this.alicePeer.getPeerID());
        Assertions.assertEquals(5, identityAssurance);
    }

    /**
     * Sorted message handling between Alice and Bob without timestamp.
     * Alice send message bob receive, and send another one, and then send a replyTo message
     */
    @Test
    public void test1_8() throws SharkException, IOException, InterruptedException {
        this.setUpScenario_1();

        SortedMessageFactory aliceSortedMessageFactory = new SortedSharkMessageFactory();
        SortedMessageFactory bobSortedMessageFactory = new SortedSharkMessageFactory();
        SortedMessage aliceSortedMessage = aliceSortedMessageFactory.produceSortedMessage(MESSAGE_BYTE, null);
        // Alice sends a message to Bob
        this.aliceMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(aliceSortedMessage), URI, this.bobPeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // Test results Bob received message
        SharkMessengerChannel bobChannel = this.bobMessenger.getChannel(URI);
        SharkMessage bobSharkMessage = bobChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertTrue(bobSharkMessage.couldBeDecrypted());
        Assertions.assertTrue(bobSharkMessage.encrypted());
        Assertions.assertTrue(bobSharkMessage.verified());
        // message received by Bob from Alice?
        Assertions.assertTrue(this.alicePeer.samePeer(bobSharkMessage.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage bobSortedMessage = SortedMessageImpl.byteArrayToSortedMessage(bobSharkMessage.getContent());
        // Add the received message to the factory
        bobSortedMessageFactory.addIncomingSortedMessage(bobSortedMessage);
        // Check sortedMessage id
        Assertions.assertEquals(aliceSortedMessage.getID(), bobSortedMessage.getID());

        Assertions.assertArrayEquals(MESSAGE_BYTE, bobSortedMessage.getContent());

        // SortedMessage parents should be []
        Assertions.assertEquals(0, bobSortedMessage.getParents().size());

        // Bob send a message to Alice
        SortedMessage bobSortedMessage_1 = bobSortedMessageFactory.produceSortedMessage(MESSAGE_1_BYTE, null);
        this.bobMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(bobSortedMessage_1), URI, this.alicePeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Bob - Alice ////////////////////////////////////////////////////
        this.runEncounter(this.bobPeer, this.alicePeer, true);

        // Test results Bob received message
        SharkMessengerChannel aliceChannel = this.aliceMessenger.getChannel(URI);
        SharkMessage aliceSharkMessage_1 = aliceChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertTrue(aliceSharkMessage_1.couldBeDecrypted());
        Assertions.assertTrue(aliceSharkMessage_1.encrypted());
        Assertions.assertTrue(aliceSharkMessage_1.verified());
        // message received by Alice from Bob?
        Assertions.assertTrue(this.bobPeer.samePeer(aliceSharkMessage_1.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage aliceSortedMessage_1 = SortedMessageImpl.byteArrayToSortedMessage(aliceSharkMessage_1.getContent());
        // Add the received message to the factory
        aliceSortedMessageFactory.addIncomingSortedMessage(bobSortedMessage_1);
        // Check sortedMessage id
        Assertions.assertEquals(bobSortedMessage_1.getID(), aliceSortedMessage_1.getID());

        Assertions.assertArrayEquals(MESSAGE_1_BYTE, aliceSortedMessage_1.getContent());

        // SortedMessage parents should be ["id"] bobSortedMessage.getID()
        Assertions.assertEquals(1, aliceSortedMessage_1.getParents().size());
        Assertions.assertTrue(aliceSortedMessage_1.getParents().contains(bobSortedMessage.getID()));

        // Bob send a message to Alice with replyTo relation to bobSortedMessage_1
        SortedMessage bobSortedMessage_2 = bobSortedMessageFactory.produceSortedMessage(MESSAGE_2_BYTE, bobSortedMessage.getID());
        this.bobMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(bobSortedMessage_2), URI, this.alicePeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Bob - Alice ////////////////////////////////////////////////////
        this.runEncounter(this.bobPeer, this.alicePeer, true);

        // Test results Bob received message
        SharkMessage aliceSharkMessage_2 = aliceChannel.getMessages().getSharkMessage(1, true);
        Assertions.assertTrue(aliceSharkMessage_2.couldBeDecrypted());
        Assertions.assertTrue(aliceSharkMessage_2.encrypted());
        Assertions.assertTrue(aliceSharkMessage_2.verified());
        // message received by Alice from Bob?
        Assertions.assertTrue(this.bobPeer.samePeer(aliceSharkMessage_2.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage aliceSortedMessage_2 = SortedMessageImpl.byteArrayToSortedMessage(aliceSharkMessage_2.getContent());
        // Add the received message to the factory
        aliceSortedMessageFactory.addIncomingSortedMessage(aliceSortedMessage_2);
        // Check sortedMessage id
        Assertions.assertEquals(bobSortedMessage_2.getID(), aliceSortedMessage_2.getID());

        Assertions.assertArrayEquals(MESSAGE_2_BYTE, aliceSortedMessage_2.getContent());

        // SortedMessage parents should be ["id"] bobSortedMessage_1.getID()
        Assertions.assertEquals(1, bobSortedMessage_2.getParents().size());
        Assertions.assertTrue(bobSortedMessage_2.getParents().contains(aliceSortedMessage_1.getID()));

        // SortedMessage reply To should be bobSortedMessage.getID()
        Assertions.assertEquals(bobSortedMessage_2.getReplyTo(), bobSortedMessage.getID());
    }

    /**
     * Sorted message handling between Alice, Bob, Clara and Tina without timestamp.
     * Alice send message Bob receive, and send another one, and then send a replyTo message
     */
    @Test
    public void test1_9() throws SharkException, IOException, InterruptedException {
        this.setUpScenario_2();

        SortedMessageFactory aliceSortedMessageFactory = new SortedSharkMessageFactory();
        SortedMessageFactory bobSortedMessageFactory = new SortedSharkMessageFactory();
        SortedMessageFactory claraSortedMessageFactory = new SortedSharkMessageFactory();
        SortedMessageFactory davidSortedMessageFactory = new SortedSharkMessageFactory();

        // Alice sends a message to Bob
        SortedMessage aliceSortedMessage = aliceSortedMessageFactory.produceSortedMessage(MESSAGE_BYTE, null);
        this.aliceMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(aliceSortedMessage), URI, this.bobPeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // Test results Bob received message
        SharkMessengerChannel bobChannel = this.bobMessenger.getChannel(URI);
        SharkMessage bobSharkMessage = bobChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertTrue(bobSharkMessage.couldBeDecrypted());
        Assertions.assertTrue(bobSharkMessage.encrypted());
        Assertions.assertTrue(bobSharkMessage.verified());
        // message received by Bob from Alice?
        Assertions.assertTrue(this.alicePeer.samePeer(bobSharkMessage.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage bobSortedMessage = SortedMessageImpl.byteArrayToSortedMessage(bobSharkMessage.getContent());
        // Add the received message to the factory
        bobSortedMessageFactory.addIncomingSortedMessage(bobSortedMessage);
        // Check sortedMessage id
        Assertions.assertEquals(aliceSortedMessage.getID(), bobSortedMessage.getID());

        Assertions.assertArrayEquals(MESSAGE_BYTE, bobSortedMessage.getContent());

        // SortedMessage parents should be []
        Assertions.assertEquals(0, bobSortedMessage.getParents().size());

        // Alice sends a message to Clara
        this.aliceMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(aliceSortedMessage), URI, this.claraPeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // Test results clara received message
        SharkMessengerChannel claraChannel = this.bobMessenger.getChannel(URI);
        SharkMessage claraSharkMessage = claraChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertTrue(claraSharkMessage.couldBeDecrypted());
        Assertions.assertTrue(claraSharkMessage.encrypted());
        Assertions.assertTrue(claraSharkMessage.verified());
        // message received by Bob from Alice?
        Assertions.assertTrue(this.alicePeer.samePeer(claraSharkMessage.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage claraSortedMessage = SortedMessageImpl.byteArrayToSortedMessage(claraSharkMessage.getContent());
        // Add the received message to the factory
        claraSortedMessageFactory.addIncomingSortedMessage(claraSortedMessage);
        // Check sortedMessage id
        Assertions.assertEquals(aliceSortedMessage.getID(), claraSortedMessage.getID());

        Assertions.assertArrayEquals(MESSAGE_BYTE, claraSortedMessage.getContent());

        // SortedMessage parents should be []
        Assertions.assertEquals(0, claraSortedMessage.getParents().size());

        // Bob send message to Alice
        SortedMessage bobSortedMessage_1 = bobSortedMessageFactory.produceSortedMessage(MESSAGE_1_BYTE, aliceSortedMessage.getID());
        this.bobMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(bobSortedMessage_1), URI, this.alicePeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Bob - Alice ////////////////////////////////////////////////////
        this.runEncounter(this.bobPeer, this.alicePeer, true);

        // Test results Alice received message
        SharkMessengerChannel aliceChannel = this.aliceMessenger.getChannel(URI);
        SharkMessage aliceSharkMessage_1 = aliceChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertTrue(aliceSharkMessage_1.couldBeDecrypted());
        Assertions.assertTrue(aliceSharkMessage_1.encrypted());
        Assertions.assertTrue(aliceSharkMessage_1.verified());
        // message received by Alice from Bob?
        Assertions.assertTrue(this.bobPeer.samePeer(aliceSharkMessage_1.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage aliceSortedMessage_1 = SortedMessageImpl.byteArrayToSortedMessage(aliceSharkMessage_1.getContent());
        // Add the received message to the factory
        aliceSortedMessageFactory.addIncomingSortedMessage(aliceSortedMessage_1);
        // Check sortedMessage id
        Assertions.assertEquals(bobSortedMessage_1.getID(), aliceSortedMessage_1.getID());

        Assertions.assertArrayEquals(MESSAGE_1_BYTE, aliceSortedMessage_1.getContent());

        // SortedMessage parents should be ["id"] bobSortedMessage.getID()
        Assertions.assertEquals(1, aliceSortedMessage_1.getParents().size());
        Assertions.assertTrue(aliceSortedMessage_1.getParents().contains(bobSortedMessage.getID()));

        // Clara send message to Alice
        SortedMessage claraSortedMessage_1 = claraSortedMessageFactory.produceSortedMessage(MESSAGE_BYTE, aliceSortedMessage.getID());
        this.claraMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(claraSortedMessage_1), URI, this.alicePeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Clara - Alice ////////////////////////////////////////////////////
        this.runEncounter(this.claraPeer, this.alicePeer, true);

        // Test results Alice received message
        SharkMessage aliceSharkMessage_11 = aliceChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertTrue(aliceSharkMessage_11.couldBeDecrypted());
        Assertions.assertTrue(aliceSharkMessage_11.encrypted());
        Assertions.assertTrue(aliceSharkMessage_11.verified());
        // message received by Alice from Bob?
        Assertions.assertTrue(this.claraPeer.samePeer(aliceSharkMessage_11.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage aliceSortedMessage_11 = SortedMessageImpl.byteArrayToSortedMessage(aliceSharkMessage_11.getContent());
        // Add the received message to the factory
        aliceSortedMessageFactory.addIncomingSortedMessage(aliceSortedMessage_11);
        // Check sortedMessage id
        Assertions.assertEquals(claraSortedMessage_1.getID(), aliceSortedMessage_11.getID());

        Assertions.assertArrayEquals(MESSAGE_BYTE, aliceSortedMessage_11.getContent());

        // SortedMessage parents should be ["id"] bobSortedMessage.getID()
        Assertions.assertEquals(1, aliceSortedMessage_11.getParents().size());
        Assertions.assertTrue(aliceSortedMessage_11.getParents().contains(claraSortedMessage.getID()));

        // Alice sends a response to Bob
        SortedMessage aliceSortedMessage_3 = aliceSortedMessageFactory.produceSortedMessage(MESSAGE_3_BYTE, null);
        this.aliceMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(aliceSortedMessage_3), URI, this.bobPeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Alice - Bob ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.bobPeer, true);

        // Test results Bob received message
        SharkMessage bobSharkMessage_3 = bobChannel.getMessages().getSharkMessage(3, true);
        Assertions.assertTrue(bobSharkMessage_3.couldBeDecrypted());
        Assertions.assertTrue(bobSharkMessage_3.encrypted());
        Assertions.assertTrue(bobSharkMessage_3.verified());
        // message received by Bob from Alice?
        Assertions.assertTrue(this.alicePeer.samePeer(bobSharkMessage_3.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage bobSortedMessage_3 = SortedMessageImpl.byteArrayToSortedMessage(bobSharkMessage_3.getContent());
        // Add the received message to the factory
        bobSortedMessageFactory.addIncomingSortedMessage(bobSortedMessage_3);
        // Check sortedMessage id
        Assertions.assertEquals(aliceSortedMessage_3.getID(), bobSortedMessage_3.getID());

        Assertions.assertArrayEquals(MESSAGE_3_BYTE, bobSortedMessage_3.getContent());
        // SortedMessage parents should be ["id1", "id2"] aliceSortedMessage_1.getID(), aliceSortedMessage_11.getID()
        Set<CharSequence> aliceSortedMessage_3_parents = new HashSet<>();
        aliceSortedMessage_3_parents.add(aliceSortedMessage_1.getID());
        aliceSortedMessage_3_parents.add(aliceSortedMessage_11.getID());

        Assertions.assertEquals(2, bobSortedMessage_3.getParents().size());
        Assertions.assertTrue(bobSortedMessage_3.getParents().containsAll(aliceSortedMessage_3_parents));

        // Alice Send a response to Clara
        this.aliceMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(aliceSortedMessage_3), URI, this.claraPeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter Alice - Clara ////////////////////////////////////////////////////
        this.runEncounter(this.alicePeer, this.claraPeer, true);

        // Test results Clara received message
        SharkMessage claraSharkMessage_3 = claraChannel.getMessages().getSharkMessage(3, true);
        Assertions.assertTrue(claraSharkMessage_3.couldBeDecrypted());
        Assertions.assertTrue(claraSharkMessage_3.encrypted());
        Assertions.assertTrue(claraSharkMessage_3.verified());
        // message received by Bob from Alice?
        Assertions.assertTrue(this.alicePeer.samePeer(claraSharkMessage_3.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage claraSortedMessage_3 = SortedMessageImpl.byteArrayToSortedMessage(claraSharkMessage_3.getContent());
        // Add the received message to the factory
        bobSortedMessageFactory.addIncomingSortedMessage(claraSortedMessage_3);
        // Check sortedMessage id
        Assertions.assertEquals(aliceSortedMessage_3.getID(), claraSortedMessage_3.getID());

        Assertions.assertArrayEquals(MESSAGE_3_BYTE, claraSortedMessage_3.getContent());
        // SortedMessage parents should be ["id1", "id2"] aliceSortedMessage_1.getID(), aliceSortedMessage_11.getID()
        Assertions.assertEquals(2, claraSortedMessage_3.getParents().size());
        Assertions.assertTrue(claraSortedMessage_3.getParents().containsAll(aliceSortedMessage_3_parents));

        // David came later in the chat and got only the last message from Alice
        // David answer to Alice
        // David send a response to Alice
        SortedMessage davidSortedMessage_4 = aliceSortedMessageFactory.produceSortedMessage(MESSAGE_4_BYTE, null);
        this.davidMessenger.sendSharkMessage(SortedMessageImpl.sortedMessageByteArray(davidSortedMessage_4), URI, this.alicePeer.getPeerID(), true, true);

        ///////////////////////////////// Encounter David - Alice ////////////////////////////////////////////////////
        this.runEncounter(this.davidPeer, this.alicePeer, true);

        // Test results Alice received message
        SharkMessage aliceSharkMessage_4 = aliceChannel.getMessages().getSharkMessage(0, true);
        Assertions.assertTrue(aliceSharkMessage_4.couldBeDecrypted());
        Assertions.assertTrue(aliceSharkMessage_4.encrypted());
        Assertions.assertTrue(aliceSharkMessage_4.verified());
        // message received by Alice from David?
        Assertions.assertTrue(this.davidPeer.samePeer(aliceSharkMessage_4.getSender()));
        // Convert received bytes to SortedMessage
        SortedMessage aliceSortedMessage_4 = SortedMessageImpl.byteArrayToSortedMessage(aliceSharkMessage_4.getContent());
        // Add the received message to the factory
        davidSortedMessageFactory.addIncomingSortedMessage(aliceSortedMessage_4);
        // Check sortedMessage id
        Assertions.assertEquals(davidSortedMessage_4.getID(), aliceSortedMessage_4.getID());

        Assertions.assertArrayEquals(MESSAGE_4_BYTE, aliceSortedMessage_4.getContent());
        // SortedMessage parents should be ["id"] aliceSortedMessage_3.getID()
        Assertions.assertEquals(1, aliceSortedMessage_4.getParents().size());
        Assertions.assertTrue(aliceSortedMessage_4.getParents().contains(aliceSortedMessage_3.getID()));
    }
}
