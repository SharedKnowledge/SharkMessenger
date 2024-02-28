package net.sharksystem.cmdline.sharkmessengerUI.commands.pki;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaireBuilder;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandKnownPeerArgument;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;

public class UICommandGetNumberOfKnownPeers extends UICommand {

    private final UICommandKnownPeerArgument owner;

    /**
     * Creates a command object
     *
     * @param identifier      the identifier of the command
     * @param rememberCommand if the command should be saved in the history log
     */
    public UICommandGetNumberOfKnownPeers(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                          String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.owner = new UICommandKnownPeerArgument(sharkMessengerApp);
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("Owner name: ", this.owner)
                .build();
    }

    @Override
    protected void execute() throws Exception {
        this.printTODOReimplement();
/*
        SharkPKIComponent pki = model.getPKIComponent();
        ui.printInfo(String.valueOf(pki.getNumberOfPersons()));

 */
    }

    @Override
    public String getDescription() {
        return "Returns the number of known peers.";
    }

    /**
     * @param arguments in following order:
     * <ol>
     *  <li>owner - peerID</li>
     * </ol>
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if(arguments.size() < 1) {
            return false;
        }
        
        boolean isParsable = owner.tryParse(arguments.get(0));
        return isParsable;
    }
}