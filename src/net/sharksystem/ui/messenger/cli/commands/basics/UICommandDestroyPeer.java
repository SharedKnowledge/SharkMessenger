package net.sharksystem.ui.messenger.cli.commands.basics;

import net.sharksystem.ui.messenger.cli.SharkNetMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkNetMessengerUI;
import net.sharksystem.ui.messenger.cli.commands.helper.AbstractCommandNoParameter;

public class UICommandDestroyPeer extends AbstractCommandNoParameter {
    public UICommandDestroyPeer(SharkNetMessengerApp sharkMessengerApp, SharkNetMessengerUI sharkMessengerUI,
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
