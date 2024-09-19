package net.sharksystem.ui.messenger.cli.commands.hubaccess;

import net.sharksystem.SharkException;
import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandQuestionnaire;
import net.sharksystem.ui.messenger.cli.commands.helper.Printer;
import net.sharksystem.hub.HubConnectionManager;
import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandQuestionnaireBuilder;
import net.sharksystem.ui.messenger.cli.UICommand;
import net.sharksystem.hub.peerside.HubConnector;
import net.sharksystem.hub.peerside.HubConnectorDescription;

import java.util.Collection;
import java.util.List;

public class UICommandListConnectedHubs extends UICommand {
    public UICommandListConnectedHubs(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                      String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder().build();
    }

    @Override
    protected void execute() throws Exception {
        StringBuilder sb = new StringBuilder();
        HubConnectionManager hubConnectionManager = this.getSharkMessengerApp().getHubConnectionManager();
        List<HubConnectorDescription> connectedHubs = hubConnectionManager.getConnectedHubs();

        if(connectedHubs == null || connectedHubs.isEmpty()) {
            this.getSharkMessengerApp().tellUI("no hub connections");
            return;
        }

        sb.append("number of hub connections: ");
        sb.append(connectedHubs.size());
        int i = 1;
        for(HubConnectorDescription hcd : connectedHubs) {
            sb.append("\n#");
            sb.append(i++);
            sb.append(": ");
            sb.append(Printer.getHubConnectorDescriptionAsString(hcd));
            sb.append("\npeers on hub: ");
            try {
                HubConnector hubConnector = hubConnectionManager.getHubConnector(hcd);
                Collection<CharSequence> peerIDs = hubConnector.getPeerIDs();
                sb.append(Printer.getStringListAsCommaSeparatedString(peerIDs.iterator()));
            }
            catch(SharkException se) {
                sb.append("problem accessing hub information - maybe syncing.");
            }
        }
        // TODO!!
        List<HubConnectionManager.FailedConnectionAttempt> failedConnectionAttemptsList =
                this.getSharkMessengerApp().getHubConnectionManager().getFailedConnectionAttempts();
        if(!failedConnectionAttemptsList.isEmpty()) {
            this.getPrintStream().println("failed connection attempts:");
            for(HubConnectionManager.FailedConnectionAttempt failedConnectionAttempt : failedConnectionAttemptsList) {
                this.getPrintStream().print("time: ");
                this.getPrintStream().print(DateTimeHelper.long2DateString(failedConnectionAttempt.getTimeStamp()));
                this.getPrintStream().print(" | ");
                HubDescriptionPrinter.print(
                        this.getPrintStream(), failedConnectionAttempt.getHubConnectorDescription());
            }
        }

        this.getSharkMessengerApp().tellUI(sb.toString());
    }

    @Override
    public String getDescription() {
        return "list connected hubs.";
    }

    /**
     * This command requires no arguments.
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        return true;
    }
}