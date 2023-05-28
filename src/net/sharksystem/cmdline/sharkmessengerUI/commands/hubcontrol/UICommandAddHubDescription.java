package net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol;

import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.hub.peerside.HubConnectorDescription;
import net.sharksystem.hub.peerside.TCPHubConnectorDescriptionImpl;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;

public class UICommandAddHubDescription extends UICommand {
    private UICommandStringArgument hubHost;
    private UICommandIntegerArgument hubPort;
    private UICommandBooleanArgument multiChannel;

    public UICommandAddHubDescription(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                      String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.hubHost = new UICommandStringArgument(sharkMessengerApp);
        this.hubPort = new UICommandIntegerArgument(sharkMessengerApp);
        this.multiChannel = new UICommandBooleanArgument(sharkMessengerApp);
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
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
