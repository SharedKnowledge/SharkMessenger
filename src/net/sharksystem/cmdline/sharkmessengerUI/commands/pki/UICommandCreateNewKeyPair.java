package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;

public class UICommandCreateNewKeyPair extends UICommand {

    public UICommandCreateNewKeyPair(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                     String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    public void execute() throws Exception {
        this.printTODOReimplement();
/*
        SharkPKIComponent pki = model.getPKIComponent();
        pki.createNewKeyPair();
        String creationTime = DateTimeHelper.long2DateString(pki.getKeysCreationTime());

        ui.printInfo("New RSA key pair was created at: " + creationTime);

 */
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creates a new pair of RSA keys.");
        return sb.toString();
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
