package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaireBuilder;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandKnownPeerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;

public class UICommandGetCertificationPath extends UICommand {

    private final UICommandKnownPeerArgument subject;

    public UICommandGetCertificationPath(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                         String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.subject = new UICommandKnownPeerArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("Subject name: ", this.subject)
                .build();
    }

    @Override
    public void execute() throws Exception {
        this.printTODOReimplement();
/*
        SharkPKIComponent pki = model.getPKIComponent();
        List<CharSequence> path = pki.getIdentityAssurancesCertificationPath(this.subject.getValue().getUserID());

        StringBuilder sb = new StringBuilder();
        for(CharSequence userID : path) {
            sb.append(" -> ");
            sb.append(userID);
        }
        ui.printInfo(sb.toString());

 */
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Returns the path which a certificate took.");
        return sb.toString();
    }

     /**
     * Arguments needed in this order: 
     * <p>
     * subject as UICommandKnownPeerArgument 
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if(arguments.size() < 1) {
            return false;
        }
        boolean isParsable = subject.tryParse(arguments.get(0));
        return isParsable;
    }

}
