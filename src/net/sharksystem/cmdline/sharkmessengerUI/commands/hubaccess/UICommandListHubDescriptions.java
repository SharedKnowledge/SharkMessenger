package net.sharksystem.cmdline.sharkmessengerUI.commands.hubaccess;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaire;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaireBuilder;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;

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
