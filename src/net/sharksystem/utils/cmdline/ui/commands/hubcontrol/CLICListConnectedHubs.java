package net.sharksystem.utils.cmdline.ui.commands.hubcontrol;

import net.sharksystem.hub.peerside.HubConnectorDescription;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.ui.CLICommand;

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
        HubDescriptionPrinter.print(this.getPrintStream(),
                this.getSharkMessengerApp().getSharkConnectionManager().getConnectedHubs());
    }

    @Override
    public String getDescription() {
        return "list connected hubs";
    }
}