package net.sharksystem.messenger;

import net.sharksystem.ASAPFormats;
import net.sharksystem.SharkComponent;
import net.sharksystem.SharkUnknownBehaviourException;
import net.sharksystem.pki.SharkPKIComponent;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * A decentralized messenger using ASAP - it does not require Internet access. It can take any
 * network, as unreliable as it might be, to transfer data.
 * <br/><br/>
 * Message exchange is (optionally) signed and encrypted. It makes use of the ASAPCertificateExchange component.
 * It is essential part and first demonstrator of an Android Shark application.
 * <br/><br/>
 * There are three communication modes. This messenger can be used very similar to most well-known server based
 * messengers with all it vulnerabilities. This messenger can also be used in a mode which is close to paranoia - which
 * comes in handy if revealed messages could be life-threatening. There are <b>three communication modes</b>:
 * <br/><br/>
 * <b>Stone age</b> communication mode restricts communication only to ad-hoc networks like Bluetooth. This mode even
 * prohibits peers from routing messages.
 * <br/><i>Example</i>: Alice issued a message to Bob. This message would only be delivered to Bob if both peers encounter
 * within an Ad-hoc network environment like Bluetooth. Bob will not route this original message to other peers.
 * Bob can quote Alice message, though. Receivers would be aware that this message was from Bob and not Alice.
 *
 * <br/><i>Pros</i>: Eavesdropping is theoretically possible but highly unlikely.
 * It would require an eavesdropping device that records transmitted data within a radius of this Ad-hoc network
 * (approximately 10m with Bluetooth). Message senders have best control of message receivers. They virtually have to
 * meet them.
 * <br/><i>Cons</i>: There is no routing. Each sender has to all receivers.
 * <br/><i>Summary</i>: It is called stone age because it mimics a community that is based on oral communication only.
 * Alice could say something to Bob. This message is lost right after it was uttered. Bob could tell e.g. Clara would
 * he heart from Alice. Clara would clearly recognize that it is Bobs message.
 * <br/><br/>
 *
 * <b>Bronze age</b> communication mode restricts communication to ad-hoc networks but allows routing.
 * <br/><i>Example</i>: Alice issued a message to Bob. This message would be exchanged within an Ad-hoc network.
 * Bob would also transmit this original message to another peer, e.g. Clara. It would not make any difference to
 * Clara if she has got that message from Alice directly over over a number of intermediary peers.
 *
 * <br/><i>Pros</i>:
 * <ul>
 *     <li>Risc of eavesdropping is the same as in stone age mode with one difference, see cons</li>
 *     <li>Messages are routed. Sender do not have to meet receivers. Each peer can become a messenger.</li>
 * </ul>
 * <br/><i>Cons</i>
 * <ul>
 *     <li>Sender have no control over the circumstances of message exchange. That is no problem with a Twitter like
 *     application. It could a problem with a chat that use used to exchange secure information. Signing and
 * encryption can be a good idea depending on the application scenario.</li>
 * <li>Only peer who are part of this communication system are potential receivers and messengers. The group of
 * involved peers can be managed. Information leaking is of course possible - but not with this system. It is easier
 * to find the leakage.</li>
 * </ul>
 *
 * <br/><i>Summary</i>: This communication mode mimics a community that already invented paper and letter. Issuing
 * a message can be compared to a written letter that is copied and retransmitted from each peer. (Bronze age is not
 * a precise description, though. It began with the invention of written signs but lasted until invention of wire-based
 * information exchange in the late 19th century.)
 * <br/><br/>
 *
 * <b>Network age</b> communication mode has no restrictions. Any protocol will do as basis for an ASAP encounter,
 * including ASAP Hubs, TCP - everything that is supported.
 * <br/><i>Example</i>: Alice issued a message to Bob. This message is sent over any communication channel available,
 * including E-Mail, TCP - whatever works. A message that is send into a channel would only be delivered to channel
 * member but by all available connections.
 *
 * <br/><i>Pros</i>:
 * <ul>
 *     <li>It is fast. There is only one peer required that uses e.g. Internet protocols to make message exchange
 *     very fast. Each message that reaches such peers would be available to any other peer with e.g. Internet access.
 *     ASAPHubs would also be used.
 *     </li>
 * </ul>
 * <br/><i>Cons</i>
 * <ul>
 *     <li>We have no longer control of the communication network. Encryption would prevent eavesdroppers to read
 *     information. They could still monitor who communicates with whom. Those metadata have very often more
 *     value that the actual content. The hub concept can make it more secure, though. But this java doc is not
 *     a place for lengthy discussions about e.g. address and hub rotation policies.</li>
 *     <li>When it comes to a leakage. It is far more difficult to convict a source. It could always be an intermediary
 *     router. It most cases it will.</li>
 * </ul>
 *
 * <br/><i>Summary</i>: We are (nearly) back in usual Internet messenger apps. Message exchange can be monitored by
 * the underlying network. Situation is better compared to usual messenger app providers, though. We can set up our
 * own hubs. We can frequently change peers addresses. Such strategies are most interesting and will not be discussed in
 * a java doc.
 *
 * <br/><br/>
 * A mode can be set for the whole application. Channel settings overwrite general application settings.
 * Bronze age is default setting.
 *
 * @author Thomas Schwotzer
 *
 */
@ASAPFormats(formats = {SharkMessengerComponent.SHARK_MESSENGER_FORMAT})
public interface SharkMessengerComponent extends SharkComponent {

    /*
        TODO: that is the Shark - Shared Knowledge. Find a way to allow projects to set its own application format
        and make this the end of all thinking. What we have here is Shark. Finally it is.
     */

    String SHARK_MESSENGER_FORMAT = "shark/messenger";

    // behaviour flags
    String SHARK_MESSENGER_STONE_AGE_MODE = "net.sharksystem.messenger_stone_age";
    String SHARK_MESSENGER_BRONZE_AGE_MODE = "net.sharksystem.messenger_bronze_age";
    String SHARK_MESSENGER_INTERNET_AGE_MODE = "net.sharksystem.messenger_internet_age";
    String DEFAULT_AGE = SHARK_MESSENGER_BRONZE_AGE_MODE;

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
     * @param receiver recipient list - can be null
     * @param sign      message will be signed yes / no
     * @param encrypt   message will be encrypted for recipient(s) yes / no. A message with multiple
     *                  receiver is sent a multiple copies, each encrypted with receiver' public key.
     * @throws SharkMessengerException no all certificates available to encrypt. Empty receiver list but
     *                                 encrypted flag set
     * @since 1.0
     */
    void sendSharkMessage(byte[] content, CharSequence uri, Set<CharSequence> receiver,
                          boolean sign, boolean encrypt) throws SharkMessengerException, IOException;

    /**
     * Variant. Just a single receiver
     * @see #sendSharkMessage(byte[], CharSequence, CharSequence, boolean, boolean)
     * @since 1.0
     */
    void sendSharkMessage(byte[] content, CharSequence uri, CharSequence receiver,
                          boolean sign, boolean encrypt) throws SharkMessengerException, IOException;

    /**
     * Variant. No receiver specified - send to anybody
     * @see #sendSharkMessage(byte[], CharSequence, CharSequence, boolean, boolean)
     * @since 1.0
     */
    void sendSharkMessage(byte[] content, CharSequence uri, boolean sign, boolean encrypt)
            throws SharkMessengerException, IOException;


    /**
     * Create a new channel.
     *
     * @param uri  Channel identifier
     * @param name Channel (human readable) name
     * @throws IOException
     * @throws SharkMessengerException channel already exists
     * @since 1.1
     */
    SharkMessengerClosedChannel createClosedChannel(CharSequence uri, CharSequence name)
            throws IOException, SharkMessengerException;

    /**
     * Remove a new channel.
     *
     * @param uri  Channel identifier
     * @throws IOException
     * @throws SharkMessengerException unknown channel uri
     * @since 1.1
     */
    void removeChannel(CharSequence uri) throws IOException, SharkMessengerException;

    /**
     * Remove all channels - be careful.
     * @throws IOException
     * @since 1.1
     */
    void removeAllChannels() throws IOException;

    /**
     * Set communication behaviour (stone, bronze, internet age) for a channel.
     * @param uri channel uri
     * @param behaviour behaviour
     * @throws SharkUnknownBehaviourException unknown communication behaviour
     * @throws SharkMessengerException unknown channel uri
     * @since 1.1
     */
    void setChannelBehaviour(CharSequence uri, String behaviour)
            throws SharkUnknownBehaviourException, SharkMessengerException;

    /**
     * Produces an object reference to a messenger channel with specified uri - throws an exception otherwise
     *
     * @param uri
     * @return
     * @throws SharkMessengerException
     */
    SharkMessengerChannel getChannel(CharSequence uri) throws SharkMessengerException, IOException;


    /**
     * Produces a list of active channel uris
     *
     * @return
     * @throws IOException
     * @since 1.1
     */
    List<CharSequence> getChannelUris() throws IOException, SharkMessengerException;

    /**
     * Get a collection of messages of a channel.
     * @param uri
     * @return
     * @throws SharkMessengerException no such channel
     * @throws IOException problems when reading
     */
//    Collection<SharkMessage> getSharkMessages(CharSequence uri) throws SharkMessengerException, IOException;

    /**
     *
     * @param listener
     * @since 1.0
     */
    void addSharkMessagesReceivedListener(SharkMessagesReceivedListener listener);

    /**
     *
     * @param listener
     * @since 1.0
     */
    void removeSharkMessagesReceivedListener(SharkMessagesReceivedListener listener);

    SharkPKIComponent getSharkPKI();
}