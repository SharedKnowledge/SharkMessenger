package net.sharksystem.utils.cmdline.ui.commands.hubcontrol;

import net.sharksystem.hub.peerside.HubConnectorDescription;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.*;

public class CLICRemoveHubDescription extends CLICommand {
    private CLICIntegerArgument hubIndex = new CLICIntegerArgument();

    public CLICRemoveHubDescription(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
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

        this.getSharkMessengerUI().getOutStream().println("remove hub description");
        HubDescriptionPrinter.print(this.getSharkMessengerUI().getOutStream(), hubDescription);

        this.getSharkMessengerApp().getSharkPeer().removeHubDescription(hubDescription);
    }

    @Override
    public String getDescription() {
        return "remove hub description from list";
    }
}
