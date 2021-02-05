package net.sharksystem.messenger;

import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPSecurityException;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Set;

public interface SharkMessage {
    String ANY_RECIPIENT = "SHARK_ANY_PEER";
    String ANONYMOUS = "SHARK_ANONYMOUS";
    int SIGNED_MASK = 0x1;
    int ENCRYPTED_MASK = 0x2;

    /**
     * Content - can be encrypted and signed
     * @return
     * @throws ASAPSecurityException if message could not be encrypted
     */
    byte[] getContent() throws ASAPSecurityException;

    /**
     * Sender - can be encrypted and signed
     * @return
     * @throws ASAPSecurityException if message could not be encrypted
     */
    CharSequence getSender() throws ASAPSecurityException;

    /**
     * Recipients are always visible - the only recipient is in the unencrypted head if message
     * is encrypted - maybe we change this in a later version.
     * @return
     */
    Set<CharSequence> getRecipients();

    /**
     * Not part of the transferred message - just a flag that indicates if this message could now
     * be verified. This can change over time, though. A non-verifiable message can be verified if the
     * right certificate arrives. A verifiable message can become non-verifiable due to loss of certificates
     * validity. In short: Result can change state of your local PKI
     * @return
     * @throws ASAPSecurityException if message could not be encrypted
     */
    boolean verified() throws ASAPSecurityException;

    /**
     * Not part of the transferred message - just a flag that indicates if this message is encrypted.
     * Messages are encrypted with recipients public key. Recipients could decrypt that message as long as
     * their key pair is valid. This message can not be decrypted any longer if recipients key pair changed.
     * Peer who act as intermediates cannot encrypt this message.
     * @return
     */
    boolean encrypted();

    /**
     * Can this message be decrypted? It is always true if it was not encrypted in the first place.
     * It is also true if this peer was receiver of this message and key pair has not changed.
     * @return true - message content can be read. False - encrypted content cannot be decrypted.
     */
    boolean couldBeDecrypted();

    /**
     * Creation date is produced when an object is serialized. It becomes part of the message.
     * @return
     * @throws ASAPException
     * @throws IOException
     * @throws ASAPSecurityException if message could not be encrypted
     */
    Timestamp getCreationTime() throws ASAPException, ASAPSecurityException, IOException;

    /**
     * Compare two message what creation date is earlier. It depends on local clocks. It is a hint not more.
     * Could need a better solution.
     *
     * @param message
     * @return
     * @throws ASAPException
     * @throws IOException
     * @throws ASAPSecurityException if message could not be encrypted
     */
    boolean isLaterThan(SharkMessage message) throws ASAPException, ASAPSecurityException, IOException;
}
