package net.sharksystem.messenger;

import net.sharksystem.SharkNotSupportedException;
import net.sharksystem.asap.*;
import net.sharksystem.pki.SharkPKIComponent;

import java.io.IOException;

public class SharkMessengerChannelImpl implements SharkMessengerChannel {
    private final ASAPChannel asapChannel;
    private final ASAPPeer asapPeer;
    private final SharkPKIComponent pkiComponent;

    public SharkMessengerChannelImpl(ASAPPeer asapPeer, SharkPKIComponent pkiComponent, ASAPChannel asapChannel) {
        this.asapPeer = asapPeer;
        this.asapChannel = asapChannel;
        this.pkiComponent = pkiComponent;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          settings                                             //
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setAge(SharkCommunicationAge channelAge) {
        throw new SharkNotSupportedException("not yet implemented - sorry");
    }

    @Override
    public SharkCommunicationAge getAge() {
        throw new SharkNotSupportedException("not yet implemented - sorry");
    }

    @Override
    public CharSequence getURI() throws IOException {
        return this.asapChannel.getUri();
    }

    @Override
    public boolean isStoneAge() {
        throw new SharkNotSupportedException("not yet implemented - sorry");
    }

    @Override
    public boolean isBronzeAge() {
        throw new SharkNotSupportedException("not yet implemented - sorry");
    }

    @Override
    public boolean isInternetAge() {
        throw new SharkNotSupportedException("not yet implemented - sorry");
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
