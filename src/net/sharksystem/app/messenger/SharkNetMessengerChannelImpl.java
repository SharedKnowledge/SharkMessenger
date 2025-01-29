package net.sharksystem.app.messenger;

import net.sharksystem.asap.*;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.Log;

import java.io.IOException;
import java.util.HashMap;

public class SharkNetMessengerChannelImpl implements SharkNetMessengerChannel {
    private static final String KEY_NAME_SHARK_MESSENGER_CHANNEL_NAME = "sharkMessengerChannelName";
    private static final String KEY_AGE_SHARK_MESSENGER_CHANNEL_NAME = "sharkMessengerAge";

    private final ASAPChannel asapChannel;
    private final ASAPPeer asapPeer;
    private final SharkPKIComponent pkiComponent;
    private CharSequence channelName;

    public SharkNetMessengerChannelImpl(ASAPPeer asapPeer, SharkPKIComponent pkiComponent, ASAPChannel asapChannel) {
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
    public SharkNetMessengerChannelImpl(ASAPPeer asapPeer,
                                        SharkPKIComponent pkiComponent,
                                        ASAPChannel asapChannel,
                                        CharSequence channelName) throws IOException {

        this(asapPeer, pkiComponent, asapChannel);

        if(channelName != null) {
            asapChannel.putExtraData(KEY_NAME_SHARK_MESSENGER_CHANNEL_NAME, channelName.toString());
        } else {
            asapChannel.putExtraData(KEY_NAME_SHARK_MESSENGER_CHANNEL_NAME,
                    SharkNetMessengerComponent.CHANNEL_DEFAULT_NAME);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          settings                                             //
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setAge(SharkNetCommunicationAge channelAge) {
        Log.writeLog(this, "not yet implemented");
    }

    @Override
    public SharkNetCommunicationAge getAge() {
        return SharkNetCommunicationAge.UNDEFINED;
    }

    @Override
    public CharSequence getURI() throws IOException {
        return this.asapChannel.getUri();
    }

    boolean readNameFromExtraData = false;
    public CharSequence getName() throws IOException {
        if(!readNameFromExtraData) {
            this.channelName = SharkNetMessengerComponent.CHANNEL_DEFAULT_NAME; // default
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
    public SharkNetMessageList getMessages(boolean sentMessagesOnly, boolean ordered)
            throws SharkNetMessengerException, IOException {

        try {
            return new SharkNetMessageListImpl(this.pkiComponent, this.asapChannel, sentMessagesOnly, ordered);
        }
        catch(ASAPException e) {
            throw new SharkNetMessengerException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public SharkNetMessageList getMessages() throws SharkNetMessengerException, IOException {
        return this.getMessages(false, true);
    }
}
