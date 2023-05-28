package net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandIntegerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaireBuilder;
import net.sharksystem.hub.peerside.HubConnectorDescription;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;

public class UICommandConnectHub extends UICommand {
    private UICommandIntegerArgument hubIndex;

    public UICommandConnectHub(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                               String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.hubIndex = new UICommandIntegerArgument(sharkMessengerApp);
    }

    protected void runBefore() {
        HubDescriptionPrinter.print(this.getPrintStream(),
                this.getSharkMessengerApp().getSharkPeer().getHubDescriptions());
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("choose hub index from list above", this.hubIndex)
                .build();
    }

    @Override
    protected void execute() throws Exception {
        HubConnectorDescription hubDescription =
                this.getSharkMessengerApp().getSharkPeer().getHubDescription(this.hubIndex.getValue());

        this.getSharkMessengerUI().getOutStream().println("try to connect to hub");
        HubDescriptionPrinter.print(this.getSharkMessengerUI().getOutStream(), hubDescription);

        this.getSharkMessengerApp().getHubConnectionManager().connectHub(hubDescription);
    }

    @Override
    public String getDescription() {
        return "connect to a hub";
    }
}
