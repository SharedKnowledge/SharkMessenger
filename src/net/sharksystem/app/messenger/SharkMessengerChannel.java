package net.sharksystem.app.messenger;

import java.io.IOException;

/**
 * From a technical perspective, radio channels described by a frequency like e.g. 97,7 MHz. Radio transceivers can
 * be tune to a specific channel, a specific frequency. Now, we will receive information sent over this channel, this
 * frequency.
 * <br/><br/>
 * A SharkMessengerChannel works in a similar way. It uses an URI instead of a frequency. User will receive messages
 * on that channel, once their messenger component is tuned accordingly. Our Shark component can listen to more
 * that one channel of course. We can also send information into this channel.
 * <br/><br/>
 * There are two other general differences to radio broadcasts and Internet based social networks, though.
 * Maybe the biggest difference (and advantage) of a Shark Messenger (and any Shark and ASAP based application)
 * compared to Internet based applications, especially social networks is its freedom to chose a communication path.
 * This choice can even be changed anytime.
 * <br/><br/>
 * A Shark Messenger running in <b>stone age</b> or <b>bronze mode</b> would only use ad-hoc networks for information
 * exchange. Eavesdropping becomes a challenge. There won't be any footprint of this communication in Internet. The URI
 * would become a kind of <i>code word</i>. Messengers aware of that word would exchange information. Other would not.
 * <br/><br/>
 * A set of peers can be defined who are trusted messengers. In that case, only those peers would be allowed to
 * <i>route</i> a message.
 * <br/><br/>
 * Iron mode allows routing messages over routed long range networks, e.g. Internet but also other multihop networks
 * e.g. based on LoRa.
 * <br/><br/>
 *
 * @author Thomas Schwotzer
 */
public interface SharkMessengerChannel {
    void setAge(SharkCommunicationAge channelAge);

    SharkCommunicationAge getAge();

    /**
     * Return the URI of this channel.
     * @return
     */
    CharSequence getURI() throws IOException;

    CharSequence getName() throws IOException;

    boolean isStoneAge();
    boolean isBronzeAge();
    boolean isInternetAge();

    /**
     * Produce a list of messages in this channel.
     * @param sentMessagesOnly true: only messages sent by this peer; false: also received messages
     * @param ordered true: messages are sorted by a timestamp (note1: sorting is useless when using
     *                sentMessagesOnly == true. They are already ordered. note2: timestamps are produced from
     *                distributed  and not synchronized clocks. It is not safe)
     * @return
     */
    SharkMessageList getMessages(boolean sentMessagesOnly, boolean ordered) throws SharkMessengerException, IOException;

    /**
     * Return a list of all messages (sent and received) ordered by timestamp. For comments of time stamp
     * accuracy, see comment in order parameter in the full variant of this method
     * @see #getMessages(boolean, boolean)
     * @return
     */
    SharkMessageList getMessages() throws SharkMessengerException, IOException;
}
