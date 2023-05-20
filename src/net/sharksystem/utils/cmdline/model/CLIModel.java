package net.sharksystem.utils.cmdline.model;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.messenger.*;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import net.sharksystem.utils.cmdline.CredentialReceivedListener;
import net.sharksystem.utils.cmdline.MessageReceivedListener;
import net.sharksystem.utils.cmdline.view.CLIModelStateObserver;

import java.util.ArrayList;
import java.util.List;
/* TODO: kann weg. Ganze Package kann weg. Das gesamte Projekt ist ein View. Das noch einmal in MVC zu teilen
ist nunja - viel. Auch unnötig. Das sind einfach Algorithmen und Multithreading findet in ASAP statt - nicht hier,
weshalb man auch keine Observer braucht. Das macht alles sehr wenig übersichtlich und schwer zu warten.
*/
public class CLIModel implements CLIModelInterface, CLIModelObservable {
    private static final CharSequence ROOTFOLDER = "sharkMessenger";
    private final String peerName;
    private CLIModelStateObserver observer;
    private int startPortNumber = 7000;
    private final List<String> commands;
    private SharkPeerFS sharkPeerFS;
    private SharkMessengerComponent messengerComponent;
    private SharkPKIComponent pkiComponent;


    public CLIModel(String peerName) {
        this.peerName = peerName;
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
    /*
        this.sharkPeerFS = new SharkPeerFS(this.peerName, ROOTFOLDER + "/" + this.peerName);

        // set up shark components

        // get PKI factory
        SharkPKIComponentFactory pkiComponentFactory = new SharkPKIComponentFactory();

        // tell peer
        this.sharkPeerFS.addComponent(pkiComponentFactory, SharkPKIComponent.class);

        // get messenger factory with pki component as parameter.
        SharkMessengerComponentFactory messengerComponentFactory = new SharkMessengerComponentFactory(
                (SharkPKIComponent) sharkPeerFS.getComponent(SharkPKIComponent.class));

        // tell peer
        this.sharkPeerFS.addComponent(messengerComponentFactory, SharkMessengerComponent.class);

        // all component in place - start peer
        this.sharkPeerFS.start();

        // get component to add listener
        this.messengerComponent = (SharkMessengerComponent) this.sharkPeerFS.
                getComponent(SharkMessengerComponent.class);
        this.messengerComponent.addSharkMessagesReceivedListener(new MessageReceivedListener(this));

        // get component to add listener
        this.pkiComponent = (SharkPKIComponent) this.sharkPeerFS.getComponent(SharkPKIComponent.class);
        this.pkiComponent.setSharkCredentialReceivedListener(new CredentialReceivedListener(this));
     */
    }

    @Override
    public void registerObserver(CLIModelStateObserver observer) {
        this.observer = observer;
    }

}
