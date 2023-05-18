package net.sharksystem.utils.cmdline;

import net.sharksystem.SharkPeer;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.utils.cmdline.model.CLIModel;

/**
 * Proposed and suggested pattern for Shark app. Implement a central entity (could even be a singleton)
 * that provides access to any component that is part of this application
 */
public class SharkMessengerApp {
//    private static SharkMessengerApp instance;
    private final CLIModel model;

    /*
    private static SharkMessengerApp sharkMessengerApp() {
        if(SharkMessengerApp.instance == null) {
            SharkMessengerApp.instance = new SharkMessengerApp();
        }

        return SharkMessengerApp.instance;
    }*/

    SharkMessengerApp() {
        this.model = new CLIModel();
    }

    /**
     * @deprecated use SharkMessengerApp
     */
    public CLIModel getCLIModel() {
        return this.model;
    }

    public SharkPeer getSharkPeer() {
        return this.model.getPeer();
    }

    public SharkMessengerComponent getMessengerComponent() {
        // TODO remove model
        return this.getCLIModel().getMessengerComponent();
    }
}
