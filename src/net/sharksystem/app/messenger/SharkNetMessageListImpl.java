package net.sharksystem.app.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.asap.*;
import net.sharksystem.pki.SharkPKIComponent;

import java.io.IOException;
import java.util.List;

public class SharkNetMessageListImpl implements SharkNetMessageList {
    private final SharkPKIComponent pkiComponent;
    private final ASAPMessages asapMessages;

    public SharkNetMessageListImpl(SharkPKIComponent pkiComponent, ASAPChannel asapChannel,
                                   boolean sentMessagesOnly, boolean ordered) throws IOException, ASAPException {
        this.pkiComponent = pkiComponent;

        if(sentMessagesOnly) {
            this.asapMessages = asapChannel.getMessages();
        } else {
            if(ordered) {
                this.asapMessages = asapChannel.getMessages(new SharkNetMessageComparison(pkiComponent));
            } else {
                this.asapMessages = asapChannel.getMessages(false);
            }
        }
    }

    @Override
    public SharkNetMessage getSharkMessage(int position, boolean chronologically) throws SharkNetMessengerException {
        try {
            List<ASAPHop> hopsList = this.asapMessages.getChunk(position, chronologically).getASAPHopList();
            byte[] content = this.asapMessages.getMessage(position, chronologically);
            return InMemoSharkNetMessage.parseMessage(content, hopsList, this.pkiComponent.getASAPKeyStore());
        }
        catch(IOException | SharkException asapException) {
            throw new SharkNetMessengerException(asapException);
        }
    }

    @Override
    public int size() throws IOException {
        return this.asapMessages.size();
    }
}
