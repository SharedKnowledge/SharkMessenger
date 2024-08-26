package net.sharksystem.messenger.cli.commands.hubaccess;

import java.util.List;

import net.sharksystem.messenger.cli.SharkMessengerApp;
import net.sharksystem.messenger.cli.SharkMessengerUI;
import net.sharksystem.messenger.cli.commandarguments.UICommandQuestionnaire;
import net.sharksystem.messenger.cli.commandarguments.UICommandQuestionnaireBuilder;
import net.sharksystem.messenger.cli.UICommand;

/**
 * List stored hub description.
 */
public class UICommandListHubDescriptions extends UICommand {
    public UICommandListHubDescriptions(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                        String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder().build();
    }

    @Override
    protected void execute() throws Exception {
        HubDescriptionPrinter.print(this.getPrintStream(),
                this.getSharkMessengerApp().getSharkPeer().getHubDescriptions());
    }

    @Override
    public String getDescription() {
        return "List known hub information.";
    }

    /**
     * This command requires no arguments.
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        return true;
    }
}
