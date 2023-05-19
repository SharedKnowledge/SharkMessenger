package net.sharksystem.utils.cmdline.ui.commands.hubcontrol;

import net.sharksystem.hub.peerside.HubConnectorDescription;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.CLICIntegerArgument;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.ui.CLICommand;

public class CLICConnectHub extends CLICommand {
    private CLICIntegerArgument hubIndex = new CLICIntegerArgument();

    public CLICConnectHub(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                          String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder()
                .addQuestion("index in hub list (produced by lsHubs)? ", this.hubIndex)
                .build();
    }

    @Override
    protected void execute() throws Exception {
        HubConnectorDescription hubDescription =
                this.getSharkMessengerApp().getSharkPeer().getHubDescription(this.hubIndex.getValue());

        this.getSharkMessengerUI().getOutStream().println("try to connect to hub");
        HubDescriptionPrinter.print(this.getSharkMessengerUI().getOutStream(), hubDescription);

        this.getSharkMessengerApp().getSharkConnectionManager().connectHub(hubDescription);
    }

    @Override
    public String getDescription() {
        return "connect to a hub";
    }
}
