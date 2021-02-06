package net.sharksystem.messenger;

import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.crypto.InMemoASAPKeyStore;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyPair;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import static net.sharksystem.messenger.TestConstants.*;

public class SharkMessageASAPSerializationTests {
    public static final String WORKING_SUB_DIRECTORY = TestConstants.ROOT_DIRECTORY
            + SharkMessageASAPSerializationTests.class.getSimpleName() + "/";
    public static final String MESSAGE = "Hi";
    public static final String URI = "sn2://all";
    public static final String ALICE_FOLDER = WORKING_SUB_DIRECTORY + ALICE_ID;

    @Test
    public void serializationTestPlain() throws ASAPException, IOException {
        byte[] serializedSNMessage = InMemoSharkMessage.serializeMessage(
                MESSAGE.getBytes(), ALICE_ID, BOB_ID);

        InMemoSharkMessage sharkNetMessage =
                InMemoSharkMessage.parseMessage(serializedSNMessage);

        Assert.assertEquals(MESSAGE, new String(sharkNetMessage.getContent()));
        Assert.assertEquals(ALICE_ID, sharkNetMessage.getSender());
        Assert.assertFalse(sharkNetMessage.verified());
        Assert.assertFalse(sharkNetMessage.encrypted());
    }

    @Test
    public void serializationTestSigned() throws ASAPException, IOException {
        InMemoASAPKeyStore keyStorageAlice = new InMemoASAPKeyStore(ALICE_ID);
        KeyPair bobKeyPair = keyStorageAlice.createTestPeer(BOB_ID); // Alice knows Bob

        InMemoASAPKeyStore keyStorageBob = new InMemoASAPKeyStore(BOB_ID, bobKeyPair,System.currentTimeMillis());
        keyStorageBob.addKeyPair(ALICE_ID, keyStorageAlice.getKeyPair()); // Bob knows Alice

        byte[] serializedSNMessage = InMemoSharkMessage.serializeMessage(
                MESSAGE.getBytes(), ALICE_ID, BOB_ID, true, false, keyStorageAlice);

        InMemoSharkMessage sharkNetMessage =
                InMemoSharkMessage.parseMessage(serializedSNMessage, keyStorageBob);

        Assert.assertEquals(MESSAGE, new String(sharkNetMessage.getContent()));
        Assert.assertEquals(ALICE_ID, sharkNetMessage.getSender());
        Assert.assertTrue(sharkNetMessage.verified());
        Assert.assertFalse(sharkNetMessage.encrypted());
    }

    @Test
    public void serializationTestSignedNotVerified() throws ASAPException, IOException {
        InMemoASAPKeyStore keyStorageAlice = new InMemoASAPKeyStore(ALICE_ID);
        KeyPair bobKeyPair = keyStorageAlice.createTestPeer(BOB_ID);
        InMemoASAPKeyStore keyStorageBob = new InMemoASAPKeyStore(BOB_ID, bobKeyPair,System.currentTimeMillis());
        // Bob does not know Alice

        byte[] serializedSNMessage = InMemoSharkMessage.serializeMessage(
                MESSAGE.getBytes(), ALICE_ID, BOB_ID, true, false, keyStorageAlice);

        InMemoSharkMessage sharkNetMessage =
                InMemoSharkMessage.parseMessage(serializedSNMessage, keyStorageBob);

        Assert.assertEquals(MESSAGE, new String(sharkNetMessage.getContent()));
        Assert.assertEquals(ALICE_ID, sharkNetMessage.getSender());
        Assert.assertFalse(sharkNetMessage.verified());
        Assert.assertFalse(sharkNetMessage.encrypted());
    }

    @Test
    public void serializationTestEncrypted() throws ASAPException, IOException {
        InMemoASAPKeyStore keyStorageAlice = new InMemoASAPKeyStore(ALICE_ID);
        KeyPair bobKeyPair = keyStorageAlice.createTestPeer(BOB_ID); // Alice knows Bob

        InMemoASAPKeyStore keyStorageBob = new InMemoASAPKeyStore(BOB_ID, bobKeyPair,System.currentTimeMillis());
        keyStorageBob.addKeyPair(ALICE_ID, keyStorageAlice.getKeyPair()); // Bob knows Alice

        byte[] serializedSNMessage = InMemoSharkMessage.serializeMessage(
                MESSAGE.getBytes(), ALICE_ID, BOB_ID, false, true,keyStorageAlice);

        InMemoSharkMessage sharkNetMessage =
                InMemoSharkMessage.parseMessage(serializedSNMessage, keyStorageBob);

        Assert.assertEquals(MESSAGE, new String(sharkNetMessage.getContent()));
        Assert.assertEquals(ALICE_ID, sharkNetMessage.getSender());
        Assert.assertFalse(sharkNetMessage.verified());
        Assert.assertTrue(sharkNetMessage.encrypted());
    }

    @Test
    public void serializationTestEncryptedAndSigned() throws ASAPException, IOException {
        InMemoASAPKeyStore keyStorageAlice = new InMemoASAPKeyStore(ALICE_ID);
        KeyPair bobKeyPair = keyStorageAlice.createTestPeer(BOB_ID); // Alice knows Bob

        InMemoASAPKeyStore keyStorageBob = new InMemoASAPKeyStore(BOB_ID, bobKeyPair,System.currentTimeMillis());
        keyStorageBob.addKeyPair(ALICE_ID, keyStorageAlice.getKeyPair()); // Bob knows Alice

        byte[] serializedSNMessage = InMemoSharkMessage.serializeMessage(
                MESSAGE.getBytes(), ALICE_ID, BOB_ID, true, true, keyStorageAlice);

        InMemoSharkMessage sharkNetMessage =
                InMemoSharkMessage.parseMessage(serializedSNMessage, keyStorageBob);

        Assert.assertEquals(MESSAGE, new String(sharkNetMessage.getContent()));
        Assert.assertEquals(ALICE_ID, sharkNetMessage.getSender());
        Assert.assertTrue(sharkNetMessage.verified());
        Assert.assertTrue(sharkNetMessage.encrypted());
    }

    @Test
    public void snTestSignedMultipleRecipients() throws ASAPException, IOException, InterruptedException {
        // Alice
        InMemoASAPKeyStore keyStorageAlice = new InMemoASAPKeyStore(ALICE_ID);

        InMemoASAPKeyStore keyStorageBob = new InMemoASAPKeyStore(BOB_ID);
        keyStorageBob.addKeyPair(ALICE_ID, keyStorageAlice.getKeyPair()); // Bob knows Alice

        Set<CharSequence> recipients = new HashSet<>();
        recipients.add(BOB_ID);
        recipients.add(CLARA_ID);
        // create Message
        byte[] asapMessage = InMemoSharkMessage.serializeMessage(
                MESSAGE.getBytes(), ALICE_ID, recipients, true, false, keyStorageAlice);

        long now = System.currentTimeMillis();

        // parse
        InMemoSharkMessage receivedMessage =
                InMemoSharkMessage.parseMessage(asapMessage, keyStorageBob);

        Assert.assertEquals(MESSAGE, new String(receivedMessage.getContent()));
        Assert.assertEquals(2, receivedMessage.getRecipients().size());
        Assert.assertEquals(ALICE_ID, receivedMessage.getSender());
        Assert.assertTrue(receivedMessage.verified());
        Assert.assertFalse(receivedMessage.encrypted());

        // check timestamp
        Timestamp creationTime = receivedMessage.getCreationTime();
        long diff = now - creationTime.getTime();
        System.out.println("diff == " + diff);
        // should not be that long
        Assert.assertTrue(diff < 100);
    }
}
