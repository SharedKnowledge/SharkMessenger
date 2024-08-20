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
import net.sharksystem.pki.PKIHelper;
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
        SharkPKIComponent pki = this.getSharkMessengerApp().getSharkPKIComponent();
        StringBuilder sb = new StringBuilder();

        sb.append("We know ");
        sb.append(pki.getNumberOfPersons());
        sb.append(" persons\n");

        boolean first = true;
        for(int i = 0; i < pki.getNumberOfPersons(); i++) {
            if(first) first = false;
            else sb.append("\n");
            sb.append(i+1);
            sb.append(": ");
            sb.append(PKIHelper.personalValue2String(pki.getPersonValuesByPosition(i)));
        }

        this.getSharkMessengerApp().tellUI(sb.toString());
    }

    @Override
    public String getDescription() {
        return "List known persons.";
    }
}
