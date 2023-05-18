package net.sharksystem.utils.cmdline.model;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.messenger.*;
import net.sharksystem.pki.CredentialMessage;
import net.sharksystem.pki.SharkCredentialReceivedListener;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import net.sharksystem.utils.cmdline.view.CLIModelStateObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CLIModel implements CLIModelInterface, CLIModelObservable {
    private static final CharSequence ROOTFOLDER = "sharkMessenger";
    private CLIModelStateObserver observer;
    private int startPortNumber = 7000;
    private final List<String> commands;
    private SharkPeerFS sharkPeerFS;
    private SharkMessengerComponent messengerComponent;
    private SharkPKIComponent pkiComponent;


    public CLIModel() {
        this.commands = new ArrayList<>();
    }

    @Override
    public SharkPeerFS getPeer() {
        return this.sharkPeerFS;
    }

    @Override
    public SharkMessengerComponent getMessengerComponent() {
        return this.messengerComponent;
    }

    @Override
    public SharkPKIComponent getPKIComponent() {
        return this.pkiComponent;
    }

    @Override
    public int getNextFreePortNumber() {
        return this.startPortNumber++;

    }

    @Override
    public void addCommandToHistory(String commandIdentifier) {
        this.commands.add(commandIdentifier);
    }

    @Override
    public String getCommandHistory() {
        StringBuilder sb = new StringBuilder();
        for(String s : this.commands){
            sb.append(s);
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    @Override
    public List<String> getCommandHistoryList() {
        return this.commands;
    }

    @Override
    public void terminate() {
        if(this.observer != null) this.observer.terminated();
    }

    @Override
    public void start() throws SharkException {
        String username = "";
        if(observer != null) username = this.observer.getUsername();

        this.sharkPeerFS = new SharkPeerFS(username, ROOTFOLDER + "/" + username);

        SharkPKIComponentFactory pkiComponentFactory = new SharkPKIComponentFactory();

        this.sharkPeerFS.addComponent(pkiComponentFactory, SharkPKIComponent.class);
        SharkMessengerComponentFactory messengerComponentFactory = new SharkMessengerComponentFactory(
                (SharkPKIComponent) sharkPeerFS.getComponent(SharkPKIComponent.class));

        this.sharkPeerFS.addComponent(messengerComponentFactory, SharkMessengerComponent.class);

        this.sharkPeerFS.start();

        this.messengerComponent = (SharkMessengerComponent) this.sharkPeerFS.
                getComponent(SharkMessengerComponent.class);

        this.messengerComponent.addSharkMessagesReceivedListener(new MessageReceivedListener());

        this.pkiComponent = (SharkPKIComponent) this.sharkPeerFS.getComponent(SharkPKIComponent.class);

        this.pkiComponent.setSharkCredentialReceivedListener(new CredentialReceivedListener());

        this.observer.started();
    }

    @Override
    public void registerObserver(CLIModelStateObserver observer) {
        this.observer = observer;
    }

    private class MessageReceivedListener implements SharkMessagesReceivedListener {
        @Override
        public void sharkMessagesReceived(CharSequence uri) {
            try {
                SharkMessageList messages = CLIModel.this.messengerComponent.getChannel(uri).getMessages();
                CLIModel.this.observer.displayMessages(messages);

            } catch (SharkMessengerException | IOException e) {
                CLIModel.this.observer.onChannelDisappeared(uri.toString());
            }
        }
    }

    private class CredentialReceivedListener implements SharkCredentialReceivedListener {

        @Override
        public void credentialReceived(CredentialMessage credentialMessage) {
            CLIModel.this.observer.displayCredentialMessage(credentialMessage);
        }
    }
}
