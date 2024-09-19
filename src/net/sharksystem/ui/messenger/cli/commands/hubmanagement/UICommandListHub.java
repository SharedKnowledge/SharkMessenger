package net.sharksystem.ui.messenger.cli.commands.hubmanagement;

import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;
import net.sharksystem.ui.messenger.cli.commands.helper.AbstractCommandNoParameter;
import net.sharksystem.ui.messenger.cli.commands.helper.Printer;

import java.util.Set;

public class UICommandListHub extends AbstractCommandNoParameter {
    public UICommandListHub(SharkMessengerApp sharkMessengerApp, SharkMessengerUI smUI, String lsHubs, boolean b) {
        super(sharkMessengerApp, smUI, lsHubs, b);
    }

    @Override
    protected void execute() throws Exception {
        Set<Integer> openHubPorts = this.getSharkMessengerApp().getOpenHubPorts();
        if(openHubPorts == null || openHubPorts.isEmpty()) {
            this.getSharkMessengerApp().tellUI("no asap hubs running in this process");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Number of ASAP Hubs running in this process: ");
        sb.append(openHubPorts.size());
        sb.append("\nlistening on following TCP ports: ");
        sb.append(Printer.getIntegerListAsCommaSeparatedString(openHubPorts.iterator()));

        this.getSharkMessengerApp().tellUI(sb.toString());
    }

    @Override
    public String getDescription() {
        return "list all hubs running locally";
    }
}
