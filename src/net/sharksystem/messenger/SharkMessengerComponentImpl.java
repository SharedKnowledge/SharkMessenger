package net.sharksystem.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.SharkNotSupportedException;
import net.sharksystem.SharkUnknownBehaviourException;
import net.sharksystem.asap.*;
import net.sharksystem.asap.utils.ASAPSerialization;
import net.sharksystem.hub.peerside.HubConnectorDescription;
import net.sharksystem.hub.peerside.HubConnectorFactory;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

class SharkMessengerComponentImpl extends SharkMessagesReceivedListenerManager
        implements SharkMessengerComponent, ASAPMessageReceivedListener {
    private static final CharSequence HUB_DESCRIPTIONS = "hubDescriptions";
    private final SharkPKIComponent sharkPKIComponent;
    private ASAPPeer asapPeer;

    public SharkMessengerComponentImpl(SharkPKIComponent sharkPKIComponent) {
        this.sharkPKIComponent = sharkPKIComponent;
        this.restoreHubDescriptions();
    }

    @Override
    public void onStart(ASAPPeer asapPeer) throws SharkException {
        this.asapPeer = asapPeer;
        Log.writeLog(this, "MAKE URI LISTENER PUBLIC AGAIN. Thank you :)");
        this.asapPeer.addASAPMessageReceivedListener(
                SharkMessengerComponent.SHARK_MESSENGER_FORMAT,
                this);

    }


    private void checkComponentRunning() throws SharkMessengerException {
        if(this.asapPeer == null || this.sharkPKIComponent == null)
            throw new SharkMessengerException("peer not started an/or pki not initialized");
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

        this.checkComponentRunning();

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

    public SharkMessengerClosedChannel createClosedChannel(CharSequence uri, CharSequence name)
            throws IOException, SharkMessengerException {

        this.checkComponentRunning();
        throw new SharkNotSupportedException("not yet implemented");
    }

    public SharkMessengerChannel getChannel(CharSequence uri) throws SharkMessengerException, IOException {
        try {
            ASAPStorage asapStorage =
                    this.asapPeer.getASAPStorage(SharkMessengerComponent.SHARK_MESSENGER_FORMAT);

            ASAPChannel channel = asapStorage.getChannel(uri);

            return new SharkMessengerChannelImpl(this.asapPeer, this.sharkPKIComponent, channel);
        }
        catch(ASAPException asapException) {
            throw new SharkMessengerException(asapException);
        }
    }

    public SharkMessengerChannel getChannel(int position) throws SharkMessengerException, IOException {
        CharSequence uri = this.getChannelUris().get(position);
        return this.getChannel(uri);
    }

    public SharkMessengerChannel createChannel(CharSequence uri, CharSequence name)
            throws SharkMessengerException, IOException {
        return this.createChannel(uri, name, true);
    }

    public SharkMessengerChannel createChannel(CharSequence uri, CharSequence name, boolean mustNotExist)
            throws SharkMessengerException, IOException {

        this.checkComponentRunning();

        try {
            this.getChannel(uri); // already exists ?
            // yes exists
            if(mustNotExist) throw new SharkMessengerException("channel already exists");
        }
        catch(SharkMessengerException asapException) {
            // does not exist yet - or it is okay
        }

        // create
        try {
            ASAPStorage asapStorage =
                    this.asapPeer.getASAPStorage(SharkMessengerComponent.SHARK_MESSENGER_FORMAT);

            asapStorage.createChannel(uri);
            ASAPChannel channel = asapStorage.getChannel(uri);

            return new SharkMessengerChannelImpl(this.asapPeer, this.sharkPKIComponent, channel, name);
        }
        catch(ASAPException asapException) {
            throw new SharkMessengerException(asapException);
        }
    }


    @Override
    public List<CharSequence> getChannelUris() throws IOException, SharkMessengerException {
        try {
            return this.asapPeer.getASAPStorage(SharkMessengerComponent.SHARK_MESSENGER_FORMAT).getChannelURIs();
        } catch(ASAPException asapException) {
                throw new SharkMessengerException(asapException);
        }
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

    /*
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
     */

    @Override
    public SharkPKIComponent getSharkPKI() {
        return this.sharkPKIComponent;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                     act on received messages                            //
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void asapMessagesReceived(ASAPMessages asapMessages,
                                     String senderE2E, // E2E part
                                     List<ASAPHop> asapHops) {
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                            hub management                                             //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<HubConnectorDescription> hubConnectorDescriptions = new ArrayList<>();

    private void checkHubDescriptionsRestored() {
        if(!this.hubDescriptionsRestored) {
            this.restoreHubDescriptions();
        }
    }

    @Override
    public void addHubDescription(HubConnectorDescription hubConnectorDescription) {
        this.checkHubDescriptionsRestored();
        this.hubConnectorDescriptions.add(hubConnectorDescription);
        this.persistHubDescriptions();
    }

    @Override
    public void removeHubDescription(HubConnectorDescription hubConnectorDescription) {
        this.checkHubDescriptionsRestored();
        HubConnectorDescription same = null;
        for(HubConnectorDescription hcd : this.hubConnectorDescriptions) {
            if(hubConnectorDescription.isSame(hcd)) {
                same = hcd;
                break;
            }
        }

        if(same != null) this.hubConnectorDescriptions.remove(same);
        this.persistHubDescriptions();
    }

    @Override
    public Collection<HubConnectorDescription> getHubDescriptions(HubConnectorDescription hubConnectorDescription) {
        this.checkHubDescriptionsRestored();
        return this.hubConnectorDescriptions;
    }

    @Override
    public HubConnectorDescription getHubDescriptions(int index) throws SharkMessengerException {
        this.checkHubDescriptionsRestored();
        if(this.hubConnectorDescriptions.size() <= index) throw new SharkMessengerException("index out of range");

        return this.hubConnectorDescriptions.get(index);
    }

    private void persistHubDescriptions() {
        // not yet started or nothing to do
        if(this.hubConnectorDescriptions.isEmpty() || this.asapPeer == null) return;

        byte[][] serializedDescriptions = new byte[this.hubConnectorDescriptions.size()][];
        int index = 0;
        try {
            for(HubConnectorDescription hcd : this.hubConnectorDescriptions) {
                    serializedDescriptions[index++] = hcd.serialize();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ASAPSerialization.writeByteArray(serializedDescriptions, baos);
            byte[] serial = baos.toByteArray();

            this.asapPeer.putExtra(HUB_DESCRIPTIONS, serial);
        } catch (IOException | ASAPException e) {
            Log.writeLogErr(this, "cannot serialized hub description");
            return;
        }

    }

    private boolean hubDescriptionsRestored = false;
    private void restoreHubDescriptions() {
        if(this.asapPeer == null) return; // not yet started
        if(this.hubDescriptionsRestored) return; // only once

        this.hubDescriptionsRestored = true;

        byte[] serial = null;
        try {
            serial = this.asapPeer.getExtra(HUB_DESCRIPTIONS);
            if(serial == null) return; // ok - no descriptions stored
        } catch (ASAPException | IOException e) {
            Log.writeLog(this, "cannot read hub description - ok, maybe there are non");
            return;
        }

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(serial);
            byte[][] serializedDescriptions = ASAPSerialization.readByte2DimArray(bais);

            for(int i = 0; i < serializedDescriptions.length; i++) {
                this.hubConnectorDescriptions.add(
                        HubConnectorFactory.createHubConnectorByDescription(serializedDescriptions[i]));
            }
        } catch (IOException | ASAPException e) {
            Log.writeLogErr(this, "cannot deserialize hub description - seems to be a bug");
        }
    }
}
