package net.sharksystem.utils.cmdline.ui.commands.hubcontrol;

import net.sharksystem.hub.peerside.HubConnectorDescription;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.ui.CLICommand;

import java.util.Collection;

/**
 * list stored hub description. This command has no parameters
 */
public class CLICListHubDescription extends CLICommand {
    public CLICListHubDescription(SharkMessengerApp sharkMessengerApp, String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, identifier, rememberCommand);
    }

    @Override
    protected CLICQuestionnaire specifyCommandStructure() {
        // no parameter
        return new CLICQuestionnaireBuilder().build();
    }

    @Override
    protected void execute() throws Exception {
        Collection<HubConnectorDescription> hubDescriptions =
                this.getSharkMessengerApp().getSharkPeer().getHubDescriptions();

        if(hubDescriptions == null || hubDescriptions.isEmpty()) {
            this.getPrintStream().println("no hub descriptions available");
        } else {
            for(HubConnectorDescription hcd : hubDescriptions) {
                HubDescriptionPrinter.print(this.getPrintStream(), hcd);
            }
        }
    }

    @Override
    public String getDescription() {
        return "list known hub information";
    }
}
