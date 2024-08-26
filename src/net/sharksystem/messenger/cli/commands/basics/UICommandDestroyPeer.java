package net.sharksystem.messenger.cli.commands.basics;

import net.sharksystem.messenger.cli.SharkMessengerApp;
import net.sharksystem.messenger.cli.SharkMessengerUI;
import net.sharksystem.messenger.cli.commands.helper.AbstractCommandNoParameter;

public class UICommandDestroyPeer extends AbstractCommandNoParameter {
    public UICommandDestroyPeer(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    public void execute() throws Exception {
        this.getSharkMessengerApp().destroyAllData();
        System.exit(1);
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("BE CAREFUL. ALL PEER DATA WILL BE DELETED. Application stops.");
        return sb.toString();
    }
}
