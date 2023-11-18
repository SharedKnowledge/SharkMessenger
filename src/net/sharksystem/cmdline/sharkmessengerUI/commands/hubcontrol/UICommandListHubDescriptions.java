package net.sharksystem.cmdline.sharkmessengerUI.commands.hubcontrol;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaireBuilder;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;

/**
 * list stored hub description. This command has no parameters
 */
public class UICommandListHubDescriptions extends UICommand {
    public UICommandListHubDescriptions(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                        String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        // no parameter
        return new UICommandQuestionnaireBuilder().build();
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

    /**
     * Arguments needed in this order: 
     * <p>
     * none
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        return true;
    }
}
