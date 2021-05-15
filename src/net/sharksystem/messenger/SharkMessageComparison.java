package net.sharksystem.messenger;

import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPMessageCompare;
import net.sharksystem.pki.SharkPKIComponent;

import java.io.IOException;
import java.sql.Timestamp;

class SharkMessageComparison implements ASAPMessageCompare {
    private final SharkPKIComponent pki;

    SharkMessageComparison(SharkPKIComponent pki) {
        this.pki = pki;
    }

    @Override
    public boolean earlier(byte[] msgA, byte[] msgB) {
        try {
            InMemoSharkMessage sharkMsgA = InMemoSharkMessage.parseMessage(msgA, this.pki);
            InMemoSharkMessage sharkMsgB = InMemoSharkMessage.parseMessage(msgB, this.pki);

            long creationTimeA = -1;
            long creationTimeB = -1;

            if(sharkMsgA.couldBeDecrypted()) {
                creationTimeA = sharkMsgA.getCreationTime().getTime();
            }
            if(sharkMsgB.couldBeDecrypted()) {
                creationTimeB = sharkMsgA.getCreationTime().getTime();
            }

            return creationTimeA < creationTimeB;

        } catch (IOException | ASAPException e) {
            // no choice: interface prevents me from throwing an exception
            return false;
        }
    }
}
