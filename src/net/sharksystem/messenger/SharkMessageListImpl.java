package net.sharksystem.messenger;

import net.sharksystem.SharkNotSupportedException;
import net.sharksystem.asap.ASAPChannel;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPMessageCompare;
import net.sharksystem.asap.ASAPMessages;
import net.sharksystem.pki.SharkPKIComponent;

import java.io.IOException;

public class SharkMessageListImpl implements SharkMessageList {
    private final SharkPKIComponent pkiComponent;
    private final ASAPMessages asapMessages;

    public SharkMessageListImpl(SharkPKIComponent pkiComponent, ASAPChannel asapChannel,
                    boolean sentMessagesOnly, boolean ordered) throws IOException, ASAPException {
        this.pkiComponent = pkiComponent;

        if(sentMessagesOnly) {
            this.asapMessages = asapChannel.getMessages();
        } else {
            if(ordered) {
                this.asapMessages = asapChannel.getMessages(new SharkMessageComparison(pkiComponent));
            } else {
                this.asapMessages = asapChannel.getMessages(false);
            }
        }
    }

    @Override
    public SharkMessage getSharkMessage(int position, boolean chronologically) throws SharkMessengerException {
        try {
            return InMemoSharkMessage.parseMessage(
                    asapMessages.getMessage(position, chronologically),
                    this.pkiComponent);
        }
        catch(ASAPException | IOException asapException) {
            throw new SharkMessengerException(asapException);
        }
    }

    @Override
    public int size() throws IOException {
        return this.asapMessages.size();
    }
}
