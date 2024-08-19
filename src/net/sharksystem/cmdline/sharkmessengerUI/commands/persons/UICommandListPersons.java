package net.sharksystem.cmdline.sharkmessengerUI.commands.persons;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaire;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaireBuilder;
import net.sharksystem.cmdline.sharkmessengerUI.commands.extendedMessenger.ChannelPrinter;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandNoParameter;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerException;
import net.sharksystem.pki.SharkPKIComponent;

import java.io.IOException;
import java.util.List;

public class UICommandListPersons extends AbstractCommandNoParameter {
    public UICommandListPersons(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected void execute() throws Exception {
//        try {
            SharkPKIComponent pki = this.getSharkMessengerApp().getSharkPKIComponent();
            this.getSharkMessengerApp().tellUI(pki.getNumberOfPersons() + " persons known");
            /*
        } catch (SharkMessengerException | IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
             */
    }

    @Override
    public String getDescription() {
        return "List known persons.";
    }
}
