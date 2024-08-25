package net.sharksystem.messenger;

import net.sharksystem.asap.*;
import net.sharksystem.pki.SharkPKIComponent;

import java.io.IOException;
import java.util.List;

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
            List<ASAPHop> hopsList = this.asapMessages.getChunk(position, chronologically).getASAPHopList();
            byte[] content = this.asapMessages.getMessage(position, chronologically);
            return InMemoSharkMessage.parseMessage(content, hopsList, this.pkiComponent.getASAPKeyStore());
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
