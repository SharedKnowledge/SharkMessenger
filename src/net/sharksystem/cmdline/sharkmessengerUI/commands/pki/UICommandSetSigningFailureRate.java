package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.*;

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
     * Arguments needed in this order: 
     * <p>
     * subject as UICommandKnownPeerArgument
     * <p>
     * failureRate as UICommandIntegerArgument
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if(arguments.size() < 2) {
            return false;
        }
        boolean isParsable = subject.tryParse(arguments.get(0)) && failureRate.tryParse(arguments.get(1));
        return isParsable;
    }

}
