package net.sharksystem.messenger;

import net.sharksystem.ASAPFormats;
import net.sharksystem.SharkComponent;

import java.io.IOException;
import java.util.Set;

/**
 * A decentralized messenger using ASAP - it does not require Internet access. It can take any
 * network, as unreliable as it might be, to transfer data.
 *
 * Message exchange is (optionally) signed and encrypted. It makes use of the ASAPCertificateExchange component.
 * It is essential part and first demonstrator of an Android Shark application.
 */
@ASAPFormats(formats = {SharkMessengerComponent.SHARK_MESSENGER_FORMAT})
public interface SharkMessengerComponent extends SharkComponent {
    String SHARK_MESSENGER_FORMAT = "shark/messenger";

    /**
     * Send a shark message. Recipients can be empty (null). This message is sent to anybody.
     * End-to-end security is supported. This message is encrypted for any recipient in a non-empty
     * recipient list if flag <i>encrypted</i> is set. Message to with an empty recipient list cannot be
     * encrypted. This message would throw an exception.
     * <br/>
     * <br/>
     * This message is signed if the signed flag is set.
     *
     * @param content   Arbitrary content
     * @param uri channel uri
     * @param recipients recipient list - can be null
     * @param sign      message will be signed yes / no
     * @param encrypt   message will be encrypted for recipient(s) yes / no. A message with multiple
     *                  recipients is sent a multiple copies, each encrypted with recipients' public key.
     * @throws SharkMessengerException no all certificates available to encrypt. Empty receiver list but
     *                                 encrypted flag set
     */
    void sendSharkMessage(byte[] content, CharSequence uri, Set<CharSequence> recipients,
                          boolean sign, boolean encrypt) throws SharkMessengerException, IOException;

    /**
     * Variant. Just a single receiver
     * @see #sendSharkMessage(byte[], CharSequence, CharSequence, boolean, boolean)
     */
    void sendSharkMessage(byte[] content, CharSequence uri, CharSequence receiver,
                          boolean sign, boolean encrypt) throws SharkMessengerException, IOException;

    /**
     * Variant. No receiver specified - send to anybody
     * @see #sendSharkMessage(byte[], CharSequence, CharSequence, boolean, boolean)
     */
    void sendSharkMessage(byte[] content, CharSequence uri, boolean sign, boolean encrypt)
            throws SharkMessengerException, IOException;


    /**
     * Create a new channel.
     *
     * @param uri  Channel identifier
     * @param name Channel (human readable) name
     * @throws IOException
     */
    void createChannel(CharSequence uri, CharSequence name) throws IOException,  SharkMessengerException;

    void removeChannel(CharSequence uri) throws IOException,  SharkMessengerException;

    void removeAllChannels() throws IOException,  SharkMessengerException;

    /**
     * Produce information of a channel at given position - support for channel list views.
     *
     * @param position
     * @return
     */
    public SharkMessengerChannelInformation getSharkMessengerChannelInformation(int position) throws SharkMessengerException, IOException;

    /**
     * Size of this component is the number of channels.
     *
     * @return
     * @throws IOException
     */
    int size() throws IOException, SharkMessengerException;

    /**
     * Get a collection of messages of a channel.
     * @param uri
     * @return
     * @throws SharkMessengerException no such channel
     * @throws IOException problems when reading
     */
//    Collection<SharkMessage> getSharkMessages(CharSequence uri) throws SharkMessengerException, IOException;

    /**
     * Recyler view support: return a message in a channel at a position.
     * @param uri channel uri
     * @param position message position
     * @param chronologically true - oldest message comes first; false - newest comes first
     * @return
     * @throws SharkMessengerException
     * @throws IOException
     */
    SharkMessage getSharkMessage(CharSequence uri, int position,  boolean chronologically)
            throws SharkMessengerException, IOException;

    void addSharkMessagesReceivedListener(SharkMessagesReceivedListener listener);

    void removeSharkMessagesReceivedListener(SharkMessagesReceivedListener listener);
}