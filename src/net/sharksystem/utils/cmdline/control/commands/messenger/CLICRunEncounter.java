package net.sharksystem.utils.cmdline.control.commands.messenger;

import net.sharksystem.utils.cmdline.control.*;

@Deprecated
public class CLICRunEncounter extends CLICommand {

    private final CLICKnownPeerArgument peer1;
    private final CLICKnownPeerArgument peer2;
    private final CLICBooleanArgument stopExchange;


    public CLICRunEncounter(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.peer1 = new CLICKnownPeerArgument();
        this.peer2 = new CLICKnownPeerArgument();
        this.stopExchange = new CLICBooleanArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Fist peer name: ", this.peer1).
                addQuestion("Second peer name: ", this.peer2).
                addQuestion("Should the connection be closed after exchange? ", this.stopExchange).
                build();
    }

    @Override
    public void execute() throws Exception {
        //ui.printInfo("This command is weak. The encounter is simulated on the local machine over a TCP connection that can't be extended to a larger network.");
//
        //boolean stopExchange = this.stopExchange.getValue();
        //try {
        //    this.peer1.getValue().getASAPTestPeerFS().startEncounter(model.getNextFreePortNumber(), this.peer2.
        //            getValue().getASAPTestPeerFS());
        //    Thread.sleep(1000);
//
        //} catch (SharkException | IOException e) {
        //    ui.printError(e.getLocalizedMessage());
        //}
//
        //if (stopExchange) {
        //    try {
        //        Thread.sleep(1000);
        //        this.peer1.getValue().getASAPTestPeerFS().stopEncounter(this.peer2.getValue().getASAPTestPeerFS());
        //    } catch (InterruptedException ignored) {
        //    }
        //} else {
        //    ui.printInfo("Connection was established. Stop the encounter with the stopEncounter command.");
        //}
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Runs an encounter between two peers.");
        return sb.toString();
    }

}
