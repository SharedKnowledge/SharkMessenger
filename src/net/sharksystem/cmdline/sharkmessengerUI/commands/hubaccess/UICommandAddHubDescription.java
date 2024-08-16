package net.sharksystem.cmdline.sharkmessengerUI.commands.hubaccess;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.*;
import net.sharksystem.hub.peerside.HubConnectorDescription;
import net.sharksystem.hub.peerside.TCPHubConnectorDescriptionImpl;

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
        return "Define a new asap hub by its hostname, port and if it supports multichannel.";
    }

    /**
     * @param arguments in following order:
     * <ol>
     *  <li>hubHost - String</li>
     *  <li>hubPort - int</li>
     *  <li>multiChannel - boolean</li>
     * </ol>
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.size() < 3) {
            return false;
        }

        boolean isParsable = hubHost.tryParse(arguments.get(0))
            && hubPort.tryParse(arguments.get(1))
            && multiChannel.tryParse(arguments.get(2));

        return isParsable;
    }
}
