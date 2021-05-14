package net.sharksystem.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.SharkNotSupportedException;
import net.sharksystem.SharkUnknownBehaviourException;
import net.sharksystem.asap.*;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.Log;

import java.io.IOException;
import java.util.*;

class SharkMessengerComponentImpl extends SharkMessagesReceivedListenerManager
        implements SharkMessengerComponent, ASAPMessageReceivedListener {
    private static final String KEY_NAME_SHARK_MESSENGER_CHANNEL_NAME = "sharkMessengerChannelName";
    private final SharkPKIComponent sharkPKIComponent;
    private ASAPPeer asapPeer;

    public SharkMessengerComponentImpl(SharkPKIComponent sharkPKIComponent) {
        this.sharkPKIComponent = sharkPKIComponent;
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

        // lets serialize and send asap messages.
        try {
            if(encrypt && selectedRecipients != null && selectedRecipients.size() > 1) {
                // more that one receiver and encrypted. Send one message for each.
                for(CharSequence receiver : selectedRecipients) {
                    this.asapPeer.sendASAPMessage(SHARK_MESSENGER_FORMAT, uri,
                            // we have at most one receiver - this method can handle all combinations
                            InMemoSharkMessage.serializeMessage(
                                    content,
                                    this.asapPeer.getPeerID(),
                                    receiver,
                                    sign, encrypt,
                                    this.sharkPKIComponent));
                }
            } else {
                this.asapPeer.sendASAPMessage(SHARK_MESSENGER_FORMAT, uri,
                    // we have at most one receiver - this method can handle all combinations
                    InMemoSharkMessage.serializeMessage(
                            content,
                            this.asapPeer.getPeerID(),
                            selectedRecipients,
                            sign, encrypt,
                            this.sharkPKIComponent));
            }
        } catch (ASAPException e) {
            throw new SharkMessengerException("when serialising and sending message: " + e.getLocalizedMessage(), e);
        }
    }

    public SharkMessengerChannel createChannel(CharSequence uri, CharSequence name)
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
        return null;
    }

    @Override
    public void removeChannel(CharSequence uri) throws IOException, SharkMessengerException {
        Log.writeLog(this, "removeChannel", "not yet implemented");
        throw new SharkNotSupportedException("not yet implemented");
    }

    @Override
    public void removeAllChannels() throws IOException {
        Log.writeLog(this, "removeAllChannels", "not yet implemented");
        throw new SharkNotSupportedException("not yet implemented");
    }

    @Override
    public void setChannelBehaviour(CharSequence uri, String behaviour) throws SharkUnknownBehaviourException, SharkMessengerException {
        Log.writeLog(this, "setChannelBehaviour", "not yet implemented");
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
                    asapStorage.getChannel(uri).getMessages(false).getMessage(position, chronologically);

            return InMemoSharkMessage.parseMessage(asapMessage, this.sharkPKIComponent);

        }
        catch(ASAPException asapException) {
            throw new SharkMessengerException(asapException);
        }
    }

    @Override
    public SharkPKIComponent getSharkPKI() {
        return this.sharkPKIComponent;
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

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                       backdoor - remove it when finished implementing                   //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public ASAPStorage getASAPStorage() throws IOException, ASAPException {
        return this.asapPeer.getASAPStorage(SHARK_MESSENGER_FORMAT);
    }


}
