package net.sharksystem.utils.cmdline.control.commands.pki;

import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICKnownPeerArgument;
import net.sharksystem.utils.cmdline.control.CLICommand;

public class CLICGetNumberOfKnownPeers extends CLICommand {

    private final CLICKnownPeerArgument owner;

    /**
     * Creates a command object
     *
     * @param identifier      the identifier of the command
     * @param rememberCommand if the command should be saved in the history log
     */
    public CLICGetNumberOfKnownPeers(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
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
