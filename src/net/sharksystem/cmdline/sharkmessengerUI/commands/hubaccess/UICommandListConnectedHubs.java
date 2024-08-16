package net.sharksystem.cmdline.sharkmessengerUI.commands.hubaccess;

import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaire;
import net.sharksystem.hub.HubConnectionManager;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaireBuilder;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;

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
        HubDescriptionPrinter.printConnectedHubs(
                this.getPrintStream(), this.getSharkMessengerApp().getHubConnectionManager());

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
    }

    @Override
    public String getDescription() {
        return "List connected hubs.";
    }

    /**
     * This command requires no arguments.
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        return true;
    }
}