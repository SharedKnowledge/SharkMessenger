package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaireBuilder;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandKnownPeerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;

public class UICommandGetIdentityAssurance extends UICommand {

    private final UICommandKnownPeerArgument subject;

    public UICommandGetIdentityAssurance(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                         String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.subject = new UICommandKnownPeerArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("Subject peer name: ", this.subject)
                .build();
    }

    @Override
    public void execute() throws Exception {
        this.printTODOReimplement();
/*
        SharkPKIComponent pki = model.getPKIComponent();
        int iA = pki.getIdentityAssurance(this.subject.getValue().getUserID());

        StringBuilder sb = new StringBuilder();
        sb.append("Identity Assurance of ");
        sb.append(this.subject.getValue().getUserID());
        sb.append("is: ");
        sb.append(iA);

        ui.printInfo(sb.toString());

 */
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Returns the identity assurance of a specific peer.");
        return sb.toString();
    }

    /**
     * @param arguments in following order:
     * <ol>
     *  <li>subject - peerID</li>
     * </ol>
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