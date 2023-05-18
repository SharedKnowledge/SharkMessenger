package net.sharksystem.utils.cmdline.ui.commands.hubcontrol;

import net.sharksystem.hub.peerside.HubConnectorDescription;
import net.sharksystem.hub.peerside.TCPHubConnectorDescriptionImpl;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.ui.*;

public class CLICAddHubDescription extends CLICommand {
    private CLICStringArgument hubHost = new CLICStringArgument();
    private CLICIntegerArgument hubPort = new CLICIntegerArgument();
    private CLICBooleanArgument multiChannel = new CLICBooleanArgument();

    public CLICAddHubDescription(SharkMessengerApp sharkMessengerApp, String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, identifier, rememberCommand);
    }

    @Override
    protected CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder()
                .addQuestion("Hub hostname? ", this.hubHost)
                .addQuestion("Port? ", this.hubPort)
                .addQuestion("Multichannel (default=true)? ", this.multiChannel)
                .build();
    }

    @Override
    protected void execute() throws Exception {
        HubConnectorDescription hubDescription =
                new TCPHubConnectorDescriptionImpl(
                        this.hubHost.getValue(),
                        this.hubPort.getValue(),
                        this.multiChannel.getValue());

        this.getSharkMessengerApp().getSharkPeer().addHubDescription(hubDescription);
    }

    @Override
    public String getDescription() {
        return "define a new asap hub by its hostname, port and if it supports multichannel";
    }
}
