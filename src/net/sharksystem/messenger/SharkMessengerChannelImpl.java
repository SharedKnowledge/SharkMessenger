package net.sharksystem.messenger;

import net.sharksystem.SharkNotSupportedException;
import net.sharksystem.asap.*;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.Log;

import java.io.IOException;
import java.util.HashMap;

public class SharkMessengerChannelImpl implements SharkMessengerChannel {
    private static final String KEY_NAME_SHARK_MESSENGER_CHANNEL_NAME = "sharkMessengerChannelName";
    private static final String KEY_AGE_SHARK_MESSENGER_CHANNEL_NAME = "sharkMessengerAge";

    private final ASAPChannel asapChannel;
    private final ASAPPeer asapPeer;
    private final SharkPKIComponent pkiComponent;
    private CharSequence channelName;

    public SharkMessengerChannelImpl(ASAPPeer asapPeer, SharkPKIComponent pkiComponent, ASAPChannel asapChannel) {
        this.asapPeer = asapPeer;
        this.pkiComponent = pkiComponent;
        this.asapChannel = asapChannel;
    }

    /**
     * Call this constructor to set up a new channel - set a name
     * @param asapPeer
     * @param pkiComponent
     * @param asapChannel
     * @param channelName
     */
    public SharkMessengerChannelImpl(ASAPPeer asapPeer,
                SharkPKIComponent pkiComponent,
                ASAPChannel asapChannel,
                CharSequence channelName) throws IOException {

        this(asapPeer, pkiComponent, asapChannel);

        if(channelName != null) {
            asapChannel.putExtraData(KEY_NAME_SHARK_MESSENGER_CHANNEL_NAME, channelName.toString());
        } else {
            asapChannel.putExtraData(KEY_NAME_SHARK_MESSENGER_CHANNEL_NAME,
                    SharkMessengerComponent.CHANNEL_DEFAULT_NAME);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          settings                                             //
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setAge(SharkCommunicationAge channelAge) {
        Log.writeLog(this, "not yet implemented");
    }

    @Override
    public SharkCommunicationAge getAge() {
        return SharkCommunicationAge.UNDEFINED;
    }

    @Override
    public CharSequence getURI() throws IOException {
        return this.asapChannel.getUri();
    }

    boolean readNameFromExtraData = false;
    public CharSequence getName() throws IOException {
        if(!readNameFromExtraData) {
            this.channelName = SharkMessengerComponent.CHANNEL_DEFAULT_NAME; // default
            this.readNameFromExtraData = true; // remember

            // find a name
            HashMap<String, String> extraData = asapChannel.getExtraData();
            if(extraData != null) {
                String name = extraData.get(KEY_NAME_SHARK_MESSENGER_CHANNEL_NAME);
                if(name != null) this.channelName = name;
            }
        }

        return this.channelName;
    }

    @Override
    public boolean isStoneAge() {
        Log.writeLog(this, "not yet implemented");
        return false;
    }

    @Override
    public boolean isBronzeAge() {
        Log.writeLog(this, "not yet implemented");return false;
    }

    @Override
    public boolean isInternetAge() {
        Log.writeLog(this, "not yet implemented");return false;
    }

    @Override
    public SharkMessageList getMessages(boolean sentMessagesOnly, boolean ordered)
            throws SharkMessengerException, IOException {

        try {
            return new SharkMessageListImpl(this.pkiComponent, this.asapChannel, sentMessagesOnly, ordered);
        }
        catch(ASAPException e) {
            throw new SharkMessengerException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public SharkMessageList getMessages() throws SharkMessengerException, IOException {
        return this.getMessages(false, true);
    }
}
