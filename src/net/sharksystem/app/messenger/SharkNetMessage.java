package net.sharksystem.app.messenger;

import net.sharksystem.asap.*;
import net.sharksystem.asap.utils.ASAPSerialization;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * A SharkMessage is an end-to-end message. It is a message created by a sender and send to one or more recipients.
 * Receiver and sender information are to be read as End-to-End (E2E) information.
 * <br/><br/>
 * Please do not be confused with definition it the SharkMessageChannel. It allows defining a set of trusted
 * messenger peer which are allowed to
 * transfer, to route message. That is a direct point-to-point communication.
 *
 * @see SharkNetMessengerChannel
 */
public interface SharkNetMessage {
    String ANY_SHARKNET_PEER = "ANY_SHARKNET_PEER";
    String ANONYMOUS = "ANONYMOUS_SHARKNET_PEER";
    int SIGNED_MASK = 0x1;
    int ENCRYPTED_MASK = 0x2;

    /** messages contains plain bytes - no further format known */
    String SN_CONTENT_TYPE_ASAP_BYTES = "sn/bytes";

    /** messages contains a character sequences; can be deserialized by ASAPSerialization.readCharacterSequenceParameter
     * @see ASAPSerialization
     * */
    String SN_CONTENT_TYPE_ASAP_CHARACTER_SEQUENCE = "sn/characters";

    /** messages contains a character sequences; can be deserialized by
     * ASAPSerialization.readCharacterSequenceSetParameter
     * @see ASAPSerialization
     * */
    String SN_CONTENT_TYPE_ASAP_CHARACTER_SEQUENCE_SET = "sn/characterSet";

    /** messages contains a byte array (byte[]); can be deserialized by
     * ASAPSerialization.readByteArray2Dim
     * @see ASAPSerialization
     * */
    String SN_CONTENT_TYPE_ASAP_BYTE_ARRAY = "sn/byteArray";

    /** messages contains a two-dimensional byte array (byte[][]); can be deserialized by
     * ASAPSerialization.readByteArray2Dim
     * @see ASAPSerialization
     * */
    String SN_CONTENT_TYPE_ASAP_TWO_DIMENSIONAL_BYTE_ARRAY = "sn/2dimByteArray";

    /**
     * Content type - describes content structure. A few type that can easily handled by ASAPSerialization are
     * declared as constants in this interface.
     * @return content type
     * @throws ASAPSecurityException
     * @see ASAPSerialization
     */
    CharSequence getContentType() throws ASAPSecurityException;    /**
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
     * Not part of the transferred message - just a flag that indicates if this message could
     * be verified. This can change over time, though. A non-verifiable message can be verified if the
     * right certificate arrives. A verifiable message can become non-verifiable due to loss of certificates
     * validity. In short: Result can change state of your local PKI
     * @return yes - a signature is present and can be verified; no - there is no signature OR it can not be verified
     * @throws ASAPSecurityException if message could not be encrypted
     */
    boolean verified() throws ASAPSecurityException;

    /**
     * Message was signed or not. A present signature does not make this message more reliable. It will, if the
     * signature can be verified.
     * @return
     * @throws ASAPSecurityException
     */
    boolean signed() throws ASAPSecurityException;

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
     * @return true - message content can be read. False - encrypted content cannot be decrypted. Encrypted for
     * another receiver.
     */
    boolean couldBeDecrypted();

    /**
     * Creation date is produced when an object is serialized. It becomes part of the message.
     * @return
     * @throws ASAPException
     * @throws IOException
     * @throws ASAPSecurityException if message could not be encrypted
     */
    long getCreationTime() throws ASAPException, ASAPSecurityException, IOException;

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
    boolean isLaterThan(SharkNetMessage message) throws ASAPException, ASAPSecurityException, IOException;

    /**
     * A message can be received directly from a sender or it was routed. This method provides the route. The first
     * entry will always be the sender. A route of length 1 describes a direct message exchange.
     * <br/><br/>
     * A peer that works in stone age mode will only work with <i>direct</i> messages.
     * <br/><br/>
     * No information about quality and safety of intermediary routing steps are provided. That is simply due to a
     * principle lack of reliability of such information.
     * @return
     */
    List<ASAPHop> getASAPHopsList();
}
