package net.sharksystem.messenger;

import net.sharksystem.app.messenger.InMemoSharkMessage;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.crypto.InMemoASAPKeyStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static net.sharksystem.messenger.TestConstants.*;

public class SharkMessageASAPSerializationTests {
    @Test
    public void serializationTestPlain() throws ASAPException, IOException {
        InMemoASAPKeyStore keyStorageAlice = new InMemoASAPKeyStore(ALICE_ID);
        byte[] serializedSNMessage = InMemoSharkMessage.serializeMessage(
                MESSAGE.getBytes(), ALICE_ID, BOB_ID);

        InMemoSharkMessage sharkNetMessage =
                InMemoSharkMessage.parseMessage(serializedSNMessage, new ArrayList<>(), keyStorageAlice);

        Assertions.assertEquals(MESSAGE, new String(sharkNetMessage.getContent()));
        Assertions.assertEquals(ALICE_ID, sharkNetMessage.getSender());
        Assertions.assertEquals(1, sharkNetMessage.getRecipients().size());
        Assertions.assertTrue(sharkNetMessage.getRecipients().contains(BOB_ID));
        Assertions.assertFalse(sharkNetMessage.verified());
        Assertions.assertFalse(sharkNetMessage.encrypted());
    }

    @Test
    public void serializationTestSigned() throws ASAPException, IOException {
        InMemoASAPKeyStore keyStorageAlice = new InMemoASAPKeyStore(ALICE_ID);
        KeyPair bobKeyPair = keyStorageAlice.createTestPeer(BOB_ID); // Alice knows Bob

        InMemoASAPKeyStore keyStorageBob = new InMemoASAPKeyStore(BOB_ID, bobKeyPair, System.currentTimeMillis());
        keyStorageBob.addKeyPair(ALICE_ID, keyStorageAlice.getKeyPair()); // Bob knows Alice

        byte[] serializedSNMessage = InMemoSharkMessage.serializeMessage(
                MESSAGE.getBytes(), ALICE_ID, BOB_ID, true, false, keyStorageAlice);

        InMemoSharkMessage sharkNetMessage =
                InMemoSharkMessage.parseMessage(serializedSNMessage, new ArrayList<>(), keyStorageBob);

        Assertions.assertEquals(MESSAGE, new String(sharkNetMessage.getContent()));
        Assertions.assertEquals(ALICE_ID, sharkNetMessage.getSender());
        Assertions.assertEquals(1, sharkNetMessage.getRecipients().size());
        Assertions.assertTrue(sharkNetMessage.getRecipients().contains(BOB_ID));
        Assertions.assertTrue(sharkNetMessage.verified());
        Assertions.assertFalse(sharkNetMessage.encrypted());
    }

    @Test
    public void serializationTestSignedNotVerified() throws ASAPException, IOException {
        InMemoASAPKeyStore keyStorageAlice = new InMemoASAPKeyStore(ALICE_ID);
        KeyPair bobKeyPair = keyStorageAlice.createTestPeer(BOB_ID); // Alice knows Bob
        keyStorageAlice.generateKeyPair(); // create Alice' key pair to sign message

        InMemoASAPKeyStore keyStorageBob = new InMemoASAPKeyStore(BOB_ID, bobKeyPair, System.currentTimeMillis());
        // Bob does not know Alice

        byte[] serializedSNMessage = InMemoSharkMessage.serializeMessage(
                MESSAGE.getBytes(), ALICE_ID, BOB_ID, true, false, keyStorageAlice);

        InMemoSharkMessage sharkNetMessage =
                InMemoSharkMessage.parseMessage(serializedSNMessage, new ArrayList<>(), keyStorageBob);

        Assertions.assertEquals(MESSAGE, new String(sharkNetMessage.getContent()));
        Assertions.assertEquals(ALICE_ID, sharkNetMessage.getSender());
        Assertions.assertEquals(1, sharkNetMessage.getRecipients().size());
        Assertions.assertTrue(sharkNetMessage.getRecipients().contains(BOB_ID));
        Assertions.assertFalse(sharkNetMessage.verified());
        Assertions.assertFalse(sharkNetMessage.encrypted());
    }

    @Test
    public void serializationTestEncrypted() throws ASAPException, IOException {
        InMemoASAPKeyStore keyStorageAlice = new InMemoASAPKeyStore(ALICE_ID);
        KeyPair bobKeyPair = keyStorageAlice.createTestPeer(BOB_ID); // Alice knows Bob

        InMemoASAPKeyStore keyStorageBob = new InMemoASAPKeyStore(BOB_ID, bobKeyPair, System.currentTimeMillis());
        keyStorageBob.addKeyPair(ALICE_ID, keyStorageAlice.getKeyPair()); // Bob knows Alice

        byte[] serializedSNMessage = InMemoSharkMessage.serializeMessage(
                MESSAGE.getBytes(), ALICE_ID, BOB_ID, false, true, keyStorageAlice);

        InMemoSharkMessage sharkNetMessage =
                InMemoSharkMessage.parseMessage(serializedSNMessage, new ArrayList<>(), keyStorageBob);

        Assertions.assertEquals(MESSAGE, new String(sharkNetMessage.getContent()));
        Assertions.assertEquals(ALICE_ID, sharkNetMessage.getSender());
        Assertions.assertEquals(1, sharkNetMessage.getRecipients().size());
        Assertions.assertTrue(sharkNetMessage.getRecipients().contains(BOB_ID));
        Assertions.assertFalse(sharkNetMessage.verified());
        Assertions.assertTrue(sharkNetMessage.encrypted());
    }

    @Test
    public void serializationTestEncryptedAndSigned() throws ASAPException, IOException {
        InMemoASAPKeyStore keyStorageAlice = new InMemoASAPKeyStore(ALICE_ID);
        KeyPair bobKeyPair = keyStorageAlice.createTestPeer(BOB_ID); // Alice knows Bob

        InMemoASAPKeyStore keyStorageBob = new InMemoASAPKeyStore(BOB_ID, bobKeyPair, System.currentTimeMillis());
        keyStorageBob.addKeyPair(ALICE_ID, keyStorageAlice.getKeyPair()); // Bob knows Alice

        byte[] serializedSNMessage = InMemoSharkMessage.serializeMessage(
                MESSAGE.getBytes(), ALICE_ID, BOB_ID, true, true, keyStorageAlice);

        InMemoSharkMessage sharkNetMessage =
                InMemoSharkMessage.parseMessage(serializedSNMessage, new ArrayList<>(), keyStorageBob);

        Assertions.assertEquals(MESSAGE, new String(sharkNetMessage.getContent()));
        Assertions.assertEquals(ALICE_ID, sharkNetMessage.getSender());
        Assertions.assertEquals(1, sharkNetMessage.getRecipients().size());
        Assertions.assertTrue(sharkNetMessage.getRecipients().contains(BOB_ID));
        Assertions.assertTrue(sharkNetMessage.verified());
        Assertions.assertTrue(sharkNetMessage.encrypted());
    }

    @Test
    public void serializationTestSignedMultipleRecipients() throws ASAPException, IOException {
        InMemoASAPKeyStore keyStorageAlice = new InMemoASAPKeyStore(ALICE_ID);

        InMemoASAPKeyStore keyStorageBob = new InMemoASAPKeyStore(BOB_ID);
        keyStorageBob.addKeyPair(ALICE_ID, keyStorageAlice.getKeyPair()); // Bob knows Alice

        // set recipients
        Set<CharSequence> recipients = new HashSet<>();
        recipients.add(BOB_ID);
        recipients.add(CLARA_ID);

        // create Message
        byte[] asapMessage = InMemoSharkMessage.serializeMessage(
                MESSAGE.getBytes(), ALICE_ID, recipients, true, false, keyStorageAlice);
        // remember when this message was created
        long now = System.currentTimeMillis();

        // parse
        InMemoSharkMessage receivedMessage =
                InMemoSharkMessage.parseMessage(asapMessage, new ArrayList<>(), keyStorageBob);

        Assertions.assertEquals(MESSAGE, new String(receivedMessage.getContent()));
        Assertions.assertEquals(2, receivedMessage.getRecipients().size());
        Assertions.assertTrue(receivedMessage.getRecipients().contains(BOB_ID));
        Assertions.assertTrue(receivedMessage.getRecipients().contains(CLARA_ID));
        Assertions.assertEquals(ALICE_ID, receivedMessage.getSender());
        Assertions.assertTrue(receivedMessage.verified());
        Assertions.assertFalse(receivedMessage.encrypted());

        // check timestamp
        long creationTime = receivedMessage.getCreationTime();
        long diff = now - creationTime;
        System.out.println("diff == " + diff);
        // should not be that long
        Assertions.assertTrue(diff < 100);
    }
}
