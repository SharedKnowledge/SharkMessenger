package net.sharksystem.messenger;

import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPHop;
import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.crypto.ASAPCryptoAlgorithms;
import net.sharksystem.asap.crypto.ASAPKeyStore;
import net.sharksystem.asap.utils.ASAPSerialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A SharkNet message is issued by a peer (sender), has content and can be tagged with an URI. It can have
 * null (to anybody), one or more recipients. A message can be signed. A message that is for a single recipient
 * can be encrypted.
 */
public class InMemoSharkMessage implements SharkMessage {
    private ASAPCryptoAlgorithms.EncryptedMessagePackage encryptedMessagePackage;
    private byte[] snContent;
    private CharSequence snSender;
    private boolean verified;
    private boolean encrypted;
    private Set<CharSequence> snRecipients;
    private Timestamp creationTime;
    private final List<ASAPHop> hopsList;

    /**
     * Received
     * @param message
     * @param sender
     * @param verified
     * @param encrypted
     */
    private InMemoSharkMessage(byte[] message, CharSequence sender,
                               Set<CharSequence> snRecipients, Timestamp creationTime,
                               boolean verified, boolean encrypted, List<ASAPHop> hopsList) {
        this.snContent = message;
        this.snSender = sender;
        this.verified = verified;
        this.encrypted = encrypted;
        this.snRecipients = snRecipients;
        this.creationTime = creationTime;
        this.hopsList = hopsList;
    }

    private InMemoSharkMessage(ASAPCryptoAlgorithms.EncryptedMessagePackage encryptedMessagePackage,
                               List<ASAPHop> hopsList) {
        this.encryptedMessagePackage = encryptedMessagePackage;
        this.snRecipients = new HashSet<>();
        this.snRecipients.add(encryptedMessagePackage.getRecipient());
        this.hopsList = hopsList;
    }

    public static byte[] serializeMessage(byte[] content, CharSequence sender, CharSequence recipient)
            throws IOException, ASAPException {

        Set<CharSequence> recipients = null;
        if(recipient != null) {
            recipients = new HashSet<>();
            recipients.add(recipient);
        }

        return InMemoSharkMessage.serializeMessage(content, sender, recipients,
                false, false, null);
    }

    public static byte[] serializeMessage(byte[] content, CharSequence sender, Set<CharSequence> recipients)
            throws IOException, ASAPException {

        return InMemoSharkMessage.serializeMessage(content, sender, recipients,
                false, false, null);
    }

    public static byte[] serializeMessage(byte[] content, CharSequence sender, CharSequence recipient,
                                   boolean sign, boolean encrypt,
                                   ASAPKeyStore ASAPKeyStore)
            throws IOException, ASAPException {

        Set<CharSequence> recipients = null;
        if(recipient != null) {
            recipients = new HashSet<>();
            recipients.add(recipient);
        }

        return InMemoSharkMessage.serializeMessage(content, sender, recipients,
                sign, encrypt, ASAPKeyStore);

    }

    public static byte[] serializeMessage(byte[] content, CharSequence sender, Set<CharSequence> receiver,
        boolean sign, boolean encrypt, ASAPKeyStore ASAPKeyStore)
            throws IOException, ASAPException {

        if( (receiver != null && receiver.size() > 1) && encrypt) {
            throw new ASAPSecurityException("cannot (yet) encrypt one message for more than one recipient - split it into more messages");
        }

        if(receiver == null || receiver.isEmpty()) {
            if(encrypt) throw new ASAPSecurityException("impossible to encrypt a message without a receiver");
            // else
            receiver = new HashSet<>();
            receiver.add(SharkMessage.ANY_RECEIVER);
        }

        if(sender == null) {
            sender = SharkMessage.ANONYMOUS;
        }

        /////////// produce serialized structure

        // merge content, sender and recipient
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ///// content
        ASAPSerialization.writeByteArray(content, baos);
        ///// sender
        ASAPSerialization.writeCharSequenceParameter(sender, baos);
        ///// receiver
        ASAPSerialization.writeCharSequenceSetParameter(receiver, baos);
        ///// timestamp
        Timestamp creationTime = new Timestamp(System.currentTimeMillis());
        String timestampString = creationTime.toString();
        ASAPSerialization.writeCharSequenceParameter(timestampString, baos);

        content = baos.toByteArray();

        byte flags = 0;
        if(sign) {
            byte[] signature = ASAPCryptoAlgorithms.sign(content, ASAPKeyStore);
            baos = new ByteArrayOutputStream();
            ASAPSerialization.writeByteArray(content, baos); // message has three parts: content, sender, receiver
            // append signature
            ASAPSerialization.writeByteArray(signature, baos);
            // attach signature to message
            content = baos.toByteArray();
            flags += SIGNED_MASK;
        }

        if(encrypt) {
            content = ASAPCryptoAlgorithms.produceEncryptedMessagePackage(
                    content,
                    receiver.iterator().next(), // already checked if one and only one is recipient
                    ASAPKeyStore);
            flags += ENCRYPTED_MASK;
        }

        // serialize SN message
        baos = new ByteArrayOutputStream();
        ASAPSerialization.writeByteParameter(flags, baos);
        ASAPSerialization.writeByteArray(content, baos);

        return baos.toByteArray();
    }

    @Override
    public byte[] getContent() throws ASAPSecurityException {
        if(this.encryptedMessagePackage != null) {
            throw new ASAPSecurityException("content could not be encrypted");
        }
        return this.snContent;
    }

    @Override
    public CharSequence getSender() throws ASAPSecurityException {
        if(this.encryptedMessagePackage != null) {
            throw new ASAPSecurityException("content could not be encrypted");
        }

        return this.snSender;
    }

    @Override
    public Set<CharSequence> getRecipients() {
        return this.snRecipients;
    }

    @Override
    public boolean verified() throws ASAPSecurityException {
        if(this.encryptedMessagePackage != null) {
            throw new ASAPSecurityException("content could not be encrypted");
        }

        return this.verified;
    }

    @Override
    public boolean encrypted() {
        return this.encrypted;
    }

    public boolean couldBeDecrypted() {
        // we have decrypted this message if there is no(!) encrypted package left
        return this.encryptedMessagePackage == null;
    }

    @Override
    public Timestamp getCreationTime() throws ASAPSecurityException {
        if(this.encryptedMessagePackage != null) {
            throw new ASAPSecurityException("content could not be encrypted");
        }

        return this.creationTime;
    }

    @Override
    public boolean isLaterThan(SharkMessage message) throws ASAPException, IOException {
        if(this.encryptedMessagePackage != null) {
            throw new ASAPSecurityException("content could not be encrypted");
        }

        Timestamp messageCreationTime = message.getCreationTime();
        return messageCreationTime.after(this.getCreationTime());
    }

    @Override
    public List<ASAPHop> getASAPHopsList() {
        return this.hopsList;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                    factory methods                                   //
    //////////////////////////////////////////////////////////////////////////////////////////

    public static InMemoSharkMessage parseMessage(byte[] message, List<ASAPHop> hopsList)
            throws IOException, ASAPException {

        return InMemoSharkMessage.parseMessage(message, hopsList);

    }

    public static InMemoSharkMessage parseMessage(byte[] message, List<ASAPHop> hopsList, ASAPKeyStore ASAPKeyStore)
            throws IOException, ASAPException {

        ByteArrayInputStream bais = new ByteArrayInputStream(message);
        byte flags = ASAPSerialization.readByte(bais);
        byte[] tmpMessage = ASAPSerialization.readByteArray(bais);

        boolean signed = (flags & SharkMessage.SIGNED_MASK) != 0;
        boolean encrypted = (flags & SharkMessage.ENCRYPTED_MASK) != 0;

        if (encrypted) {
            // decrypt
            bais = new ByteArrayInputStream(tmpMessage);
            ASAPCryptoAlgorithms.EncryptedMessagePackage
                    encryptedMessagePackage = ASAPCryptoAlgorithms.parseEncryptedMessagePackage(bais);

            // for me?
            if (!ASAPKeyStore.isOwner(encryptedMessagePackage.getRecipient())) {
                return new InMemoSharkMessage(encryptedMessagePackage, hopsList);
                //throw new ASAPException("SharkNetMessage: message not for me");
            }

            // replace message with decrypted message
            tmpMessage = ASAPCryptoAlgorithms.decryptPackage(
                    encryptedMessagePackage, ASAPKeyStore);
        }

        byte[] signature = null;
        byte[] signedMessage = null;
        if (signed) {
            // split message from signature
            bais = new ByteArrayInputStream(tmpMessage);
            tmpMessage = ASAPSerialization.readByteArray(bais);
            signedMessage = tmpMessage;
            signature = ASAPSerialization.readByteArray(bais);
        }

        ///////////////// produce object form serialized bytes
        bais = new ByteArrayInputStream(tmpMessage);

        ////// content
        byte[] snMessage = ASAPSerialization.readByteArray(bais);
        ////// sender
        String snSender = ASAPSerialization.readCharSequenceParameter(bais);
        ////// recipients
        Set<CharSequence> snReceivers = ASAPSerialization.readCharSequenceSetParameter(bais);
        ///// timestamp
        String timestampString = ASAPSerialization.readCharSequenceParameter(bais);
        Timestamp creationTime = Timestamp.valueOf(timestampString);

        boolean verified = false; // initialize
        if (signature != null) {
            try {
                verified = ASAPCryptoAlgorithms.verify(
                        signedMessage, signature, snSender, ASAPKeyStore);
            } catch (ASAPSecurityException e) {
                // verified definitely false
                verified = false;
            }
        }

        // replace special sn symbols
        return new InMemoSharkMessage(snMessage, snSender, snReceivers, creationTime, verified, encrypted, hopsList);
    }

    public boolean isAnonymousSender(CharSequence peerID) {
        return peerID.toString().equalsIgnoreCase(SharkMessage.ANONYMOUS);
    }

    public boolean isAnyRecipient(CharSequence peerID) {
        return peerID.toString().equalsIgnoreCase(SharkMessage.ANY_RECEIVER);
    }
}
