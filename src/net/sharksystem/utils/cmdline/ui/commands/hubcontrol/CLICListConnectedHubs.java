package net.sharksystem.utils.cmdline.ui.commands.hubcontrol;

import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.hub.HubConnectionManager;
import net.sharksystem.hub.peerside.HubConnectorDescription;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.ui.CLICommand;

import java.util.List;

public class CLICListConnectedHubs extends CLICommand {
    public CLICListConnectedHubs(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                 String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().build();
    }

    @Override
    protected void execute() throws Exception {
        this.getPrintStream().println("connected hubs:");
        HubDescriptionPrinter.print(this.getPrintStream(),
                this.getSharkMessengerApp().getSharkConnectionManager().getConnectedHubs());

        List<HubConnectionManager.FailedConnectionAttempt> failedConnectionAttemptsList =
                this.getSharkMessengerApp().getSharkConnectionManager().getFailedConnectionAttempts();
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
    }

    @Override
    public String getDescription() {
        return "list connected hubs";
    }
}