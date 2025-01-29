package net.sharksystem.app.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPHop;
import net.sharksystem.asap.ASAPMessageCompare;
import net.sharksystem.pki.SharkPKIComponent;

import java.io.IOException;
import java.util.ArrayList;

class SharkNetMessageComparison implements ASAPMessageCompare {
    private final SharkPKIComponent pki;

    SharkNetMessageComparison(SharkPKIComponent pki) {
        this.pki = pki;
    }

    @Override
    public boolean earlier(byte[] msgA, byte[] msgB) {
        try {
            InMemoSharkNetMessage sharkMsgA =
                    InMemoSharkNetMessage.parseMessage(msgA, new ArrayList<ASAPHop>(), this.pki.getASAPKeyStore());
            InMemoSharkNetMessage sharkMsgB =
                    InMemoSharkNetMessage.parseMessage(msgB, new ArrayList<ASAPHop>(), this.pki.getASAPKeyStore());

            long creationTimeA = -1;
            long creationTimeB = -1;

            if(sharkMsgA.couldBeDecrypted()) {
                creationTimeA = sharkMsgA.getCreationTime();
            }
            if(sharkMsgB.couldBeDecrypted()) {
                creationTimeB = sharkMsgA.getCreationTime();
            }

            return creationTimeA < creationTimeB;

        } catch (IOException | SharkException e) {
            // no choice: interface prevents me from throwing an exception
            return false;
        }
    }
}
