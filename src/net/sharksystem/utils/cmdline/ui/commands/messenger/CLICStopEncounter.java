package net.sharksystem.utils.cmdline.ui.commands.messenger;

import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.*;

@Deprecated
public class CLICStopEncounter extends CLICommand {

    private final CLICKnownPeerArgument peer1;
    private final CLICKnownPeerArgument peer2;

    public CLICStopEncounter(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                             String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.peer1 = new CLICKnownPeerArgument();
        this.peer2 = new CLICKnownPeerArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("First peer name: ", this.peer1).
                addQuestion("Second peer name: ", this.peer2).
                build();
    }

    @Override
    public void execute() throws Exception {
        //try {
            //this.peer1.getValue().getASAPTestPeerFS().stopEncounter(this.peer2.getValue().getASAPTestPeerFS());
        //} catch (SharkException | IOException e) {
         //   ui.printError(e.getLocalizedMessage());
        //
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Stops an already running encounter.");
        return sb.toString();
    }

}
