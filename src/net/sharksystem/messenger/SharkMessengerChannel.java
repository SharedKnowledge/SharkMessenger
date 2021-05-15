package net.sharksystem.messenger;

import java.io.IOException;
import java.util.Set;

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

    boolean isStoneAge();
    boolean isBronzeAge();
    boolean isInternetAge();

    int size(boolean sentMessagesOnly, boolean verifiedMessagesOnly, boolean encryptedMessagesOnly);

    SharkMessageList getMessages();
    SharkMessage getSharkMessage(int position, boolean chronologically) throws SharkMessengerException;


    SharkMessageList getMessagesBySender(CharSequence senderID);
    SharkMessageList getMessagesByReceiver(CharSequence receiverID);

}
