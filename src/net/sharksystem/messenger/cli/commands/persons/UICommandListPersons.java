package net.sharksystem.messenger.cli.commands.persons;

import net.sharksystem.messenger.cli.SharkMessengerApp;
import net.sharksystem.messenger.cli.SharkMessengerUI;
import net.sharksystem.messenger.cli.commands.helper.AbstractCommandNoParameter;
import net.sharksystem.pki.PKIHelper;
import net.sharksystem.pki.SharkPKIComponent;

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
