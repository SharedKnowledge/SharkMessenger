package net.sharksystem.ui.messenger.cli.commands.pki;

import java.util.List;

import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;
import net.sharksystem.ui.messenger.cli.UICommand;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandIntegerArgument;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandKnownPeerArgument;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandQuestionnaire;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandQuestionnaireBuilder;

public class UICommandSetSigningFailureRate extends UICommand {
    private final UICommandKnownPeerArgument subject;

    private final UICommandIntegerArgument failureRate;

    public UICommandSetSigningFailureRate(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                          String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.subject = new UICommandKnownPeerArgument(sharkMessengerApp);
        this.failureRate = new UICommandIntegerArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("Subject peer name: ", this.subject)
                .addQuestion("Failure rate: ", this.failureRate)
                .build();
    }

    @Override
    public void execute() throws Exception {
        this.printTODOReimplement();
/*
        SharkPKIComponent pki = model.getPKIComponent();
        if(this.failureRate.getValue() >= 1 && this.failureRate.getValue() <= 10) {
            pki.setSigningFailureRate(this.subject.getValue().getUserID(), this.failureRate.getValue());

            StringBuilder sb = new StringBuilder();
            sb.append("Failure rate was set to ");
            sb.append(this.failureRate.getValue());
            sb.append(" for peer ");
            sb.append(this.subject.getValue().getUserID());

            ui.printInfo(sb.toString());

        } else {
            ui.printError("Failure rate must be between 1 and 10 (1 and 10 inclusive)!");
        }

 */
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sets the signing failure rate for a specific peer.");
        return sb.toString();
    }

    /**
     * @param arguments in following order:
     * <ol>
     *  <li>subject - peerID</li>
     *  <li>failureRate - int</li>
     * </ol>
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if(arguments.size() < 2) {
            return false;
        }

        boolean isParsable = subject.tryParse(arguments.get(0)) 
                && failureRate.tryParse(arguments.get(1));

        return isParsable;
    }
}