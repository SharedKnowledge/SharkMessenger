package net.sharksystem.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.SharkUnknownBehaviourException;
import net.sharksystem.asap.*;
import net.sharksystem.asap.crypto.ASAPKeyStore;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.Log;

import java.io.IOException;
import java.util.*;

class SharkMessengerComponentImpl extends SharkMessagesReceivedListenerManager
        implements SharkMessengerComponent, ASAPMessageReceivedListener {
    private static final String KEY_NAME_SHARK_MESSENGER_CHANNEL_NAME = "sharkMessengerChannelName";
    private final SharkPKIComponent certificateComponent;
    private ASAPPeer asapPeer;

    public SharkMessengerComponentImpl(SharkPKIComponent certificateComponent) {
        this.certificateComponent = certificateComponent;
    }

    @Override
    public void onStart(ASAPPeer asapPeer) throws SharkException {
        this.asapPeer = asapPeer;
        Log.writeLog(this, "MAKE URI LISTENER PUBLIC AGAIN. Thank you :)");
        this.asapPeer.addASAPMessageReceivedListener(
                SharkMessengerComponent.SHARK_MESSENGER_FORMAT,
                this);

    }

    @Override
    public void setBehaviour(String behaviour, boolean on) throws SharkUnknownBehaviourException {
        throw new SharkUnknownBehaviourException(behaviour);
    }

    @Override
    public void sendSharkMessage(byte[] content, CharSequence uri, boolean sign,
                                 boolean encrypt) throws IOException, SharkMessengerException {
        HashSet<CharSequence> set = new HashSet();
        this.sendSharkMessage(content, uri, set, sign, encrypt);
    }

    @Override
    public void sendSharkMessage(byte[] content, CharSequence uri,
                                 CharSequence receiver, boolean sign,
                                 boolean encrypt) throws IOException, SharkMessengerException {
        HashSet<CharSequence> set = new HashSet();
        set.add(receiver);

        this.sendSharkMessage(content, uri, set, sign, encrypt);
    }

    @Override
    public void sendSharkMessage(byte[] content, CharSequence uri,
                                 Set<CharSequence> selectedRecipients, boolean sign,
                                 boolean encrypt)
            throws SharkMessengerException, IOException {

        // lets serialize and send asap messages. TODO!!!

        // 1) no recipients
        if(selectedRecipients == null || selectedRecipients.isEmpty()) {
            // no receivers - anybody
            sendSharkMessage(content, uri, sign, encrypt);
        } else {
            // 2) recipients

            // 2.1 one recipient
            if(selectedRecipients.size() == 1) {
                sendSharkMessage(
                        content, uri, selectedRecipients, sign, encrypt);
            } else {
                // 2.2. more than one recipient
                if(encrypt) {
                    // 2.2.1 encrypt with more than one recipient - create message for each
                    for(CharSequence receiver : selectedRecipients) {
                        sendSharkMessage(
                                content, receiver, uri,sign,true);
                    }
                } else {
                    // 2.2.2 no encryption with more than one receiver
                    sendSharkMessage(
                            content, uri, selectedRecipients, sign, false);
                }
            }
        }


        byte[] message = new byte[0];
        CharSequence sender = null;
        ASAPKeyStore ks = this.certificateComponent;

        try {
            byte[] serializedMessage = InMemoSharkMessage.serializeMessage(
                    content, this.asapPeer.getPeerID(),
                    selectedRecipients, sign, encrypt, this.certificateComponent);

            this.asapPeer.sendASAPMessage(
                    SharkMessengerComponent.SHARK_MESSENGER_FORMAT,
                    uri, serializedMessage);
        }
        catch(ASAPException asapException) {
            throw new SharkMessengerException(asapException);
        }
    }

    public void createChannel(CharSequence uri, CharSequence name)
            throws IOException, SharkMessengerException {

        try {
            ASAPStorage asapStorage =
                    this.asapPeer.getASAPStorage(SharkMessengerComponent.SHARK_MESSENGER_FORMAT);

            asapStorage.createChannel(uri);
            asapStorage.putExtra(uri, KEY_NAME_SHARK_MESSENGER_CHANNEL_NAME, name.toString());
        }
        catch(ASAPException asapException) {
            throw new SharkMessengerException(asapException);
        }
    }

    @Override
    public void removeChannel(CharSequence uri) throws IOException, SharkMessengerException {
        Log.writeLog(this, "removeChannel", "not yet implemented");
    }

    @Override
    public void removeAllChannels() throws IOException, SharkMessengerException {
        Log.writeLog(this, "removeAllChannels", "not yet implemented");
    }

    public SharkMessengerChannelInformation getSharkMessengerChannelInformation(int position)
            throws SharkMessengerException, IOException {
        try {
            ASAPStorage asapStorage =
                    this.asapPeer.getASAPStorage(SharkMessengerComponent.SHARK_MESSENGER_FORMAT);

            CharSequence uri = asapStorage.getChannelURIs().get(position);
            return new SharkMessengerChannelInformation(uri,
                    asapStorage.getExtra(uri, KEY_NAME_SHARK_MESSENGER_CHANNEL_NAME));
        }
        catch(ASAPException asapException) {
            throw new SharkMessengerException(asapException);
        }
    }

    public int size() throws IOException, SharkMessengerException {
        try {
            ASAPStorage asapStorage =
                    this.asapPeer.getASAPStorage(SharkMessengerComponent.SHARK_MESSENGER_FORMAT);

            return asapStorage.getChannelURIs().size();
        }
        catch(ASAPException asapException) {
            throw new SharkMessengerException(asapException);
        }
    }

    @Override
    public SharkMessage getSharkMessage(CharSequence uri, int position, boolean chronologically)
            throws SharkMessengerException, IOException {

        try {
            ASAPStorage asapStorage =
                    this.asapPeer.getASAPStorage(SharkMessengerComponent.SHARK_MESSENGER_FORMAT);

            byte[] asapMessage =
                    asapStorage.getChannel(uri).getMessages().getMessage(position, chronologically);

            return InMemoSharkMessage.parseMessage(asapMessage, this.certificateComponent);

        }
        catch(ASAPException asapException) {
            throw new SharkMessengerException(asapException);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                     act on received messages                            //
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void asapMessagesReceived(ASAPMessages asapMessages) {
        CharSequence uri = asapMessages.getURI();
        Log.writeLog(this, "MAKE URI LISTENER PUBLIC AGAIN. Thank you :)");

        this.notifySharkMessageReceivedListener(uri);
    }
}
