package net.sharksystem.messenger;

import net.sharksystem.SharkNotSupportedException;
import net.sharksystem.asap.ASAPChannel;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPPeer;
import net.sharksystem.asap.ASAPStorage;
import net.sharksystem.pki.SharkPKIComponent;

import java.io.IOException;
import java.util.Set;

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

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //                                       message handling                                        //
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public SharkMessageList getMessages() {
        return null;
    }

    @Override
    public SharkMessage getSharkMessage(int position, boolean chronologically) throws SharkMessengerException {
        try {
            ASAPStorage asapStorage =
                    this.asapPeer.getASAPStorage(SharkMessengerComponent.SHARK_MESSENGER_FORMAT);

            byte[] asapMessage =
                    asapStorage.getChannel(this.getURI()).getMessages(false).getMessage(position, chronologically);

            return InMemoSharkMessage.parseMessage(asapMessage, this.pkiComponent);

        }
        catch(ASAPException | IOException asapException) {
            throw new SharkMessengerException(asapException);
        }
    }

    @Override
    public SharkMessageList getMessagesBySender(CharSequence senderID) {
        return null;
    }

    @Override
    public SharkMessageList getMessagesByReceiver(CharSequence receiverID) {
        return null;
    }

    @Override
    public int size(boolean sentMessagesOnly, boolean verifiedMessagesOnly, boolean encryptedMessagesOnly) {
        throw new SharkNotSupportedException("not yet implemented - sorry");
    }
}
