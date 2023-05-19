package net.sharksystem.utils.cmdline.ui.commands.hubcontrol;

import net.sharksystem.hub.peerside.HubConnectorDescription;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.ui.CLICommand;

import java.util.List;

/**
 * list stored hub description. This command has no parameters
 */
public class CLICListHubDescriptions extends CLICommand {
    public CLICListHubDescriptions(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                   String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected CLICQuestionnaire specifyCommandStructure() {
        // no parameter
        return new CLICQuestionnaireBuilder().build();
    }

    @Override
    protected void execute() throws Exception {
        HubDescriptionPrinter.print(this.getPrintStream(),
                this.getSharkMessengerApp().getSharkPeer().getHubDescriptions());
    }

    @Override
    public String getDescription() {
        return "list known hub information";
    }
}
