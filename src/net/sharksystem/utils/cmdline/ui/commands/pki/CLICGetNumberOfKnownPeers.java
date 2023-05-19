package net.sharksystem.utils.cmdline.ui.commands.pki;

import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.ui.CLICKnownPeerArgument;
import net.sharksystem.utils.cmdline.ui.CLICommand;

public class CLICGetNumberOfKnownPeers extends CLICommand {

    private final CLICKnownPeerArgument owner;

    /**
     * Creates a command object
     *
     * @param identifier      the identifier of the command
     * @param rememberCommand if the command should be saved in the history log
     */
    public CLICGetNumberOfKnownPeers(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                     String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.owner = new CLICKnownPeerArgument();
    }

    @Override
    protected CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder()
                .addQuestion("Owner name: ", this.owner)
                .build();
    }

    @Override
    protected void execute() throws Exception {
        SharkPKIComponent pki = model.getPKIComponent();
        ui.printInfo(String.valueOf(pki.getNumberOfPersons()));
    }

    @Override
    public String getDescription() {
        return "Returns the number of known peers.";
    }

}
