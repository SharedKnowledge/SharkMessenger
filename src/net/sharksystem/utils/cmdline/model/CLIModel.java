package net.sharksystem.utils.cmdline.model;

import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.utils.cmdline.view.CLIModelStateObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CLIModel implements CLIModelInterface, CLIModelObservable {

    private CLIModelStateObserver observer;

    private final Map<String, SharkTestPeerFS> peers;

    private int startPortNumber = 7000;

    private final List<String> commands;
    private boolean running;

    public CLIModel() {
        this.peers = new HashMap<>();
        this.commands = new ArrayList<>();
        this.running = false;
    }

    @Override
    public void addPeer(String name, SharkTestPeerFS peer) {
        this.peers.put(name, peer);
    }

    @Override
    public boolean hasPeer(String name) {
        return this.peers.containsKey(name);
    }

    @Override
    public SharkTestPeerFS getPeer(String name) {
        return this.peers.get(name);
    }

    @Override
    public SharkMessengerComponent getMessengerFromPeer(String name) throws SharkException {
        SharkTestPeerFS peer = this.peers.get(name);
        return (SharkMessengerComponent) peer.getComponent(SharkMessengerComponent.class);
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
        this.running = false;
        if(this.observer != null) this.observer.terminated();
    }

    @Override
    public void start() {
        this.running = true;
        if(observer != null) this.observer.started();
    }

    @Override
    public void registerObserver(CLIModelStateObserver observer) {
        this.observer = observer;
    }
}
