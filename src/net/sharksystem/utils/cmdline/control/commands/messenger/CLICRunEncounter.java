package net.sharksystem.utils.cmdline.control.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.utils.cmdline.control.*;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.io.IOException;

public class CLICRunEncounter extends CLICommand {

    private final CLICSharkPeerArgument peer1;
    private final CLICSharkPeerArgument peer2;
    private final CLICBooleanArgument stopExchange;


    public CLICRunEncounter(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.peer1 = new CLICSharkPeerArgument();
        this.peer2 = new CLICSharkPeerArgument();
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
    public void execute(CLIInterface ui, CLIModelInterface model) throws Exception {
        ui.printInfo("This command is weak. The encounter is simulated on the local machine over a TCP connection that can't be extended to a larger network.");

        boolean stopExchange = this.stopExchange.getValue();
        try {
            this.peer1.getValue().getASAPTestPeerFS().startEncounter(model.getNextFreePortNumber(), this.peer2.
                    getValue().getASAPTestPeerFS());

        } catch (SharkException | IOException e) {
            ui.printError(e.getLocalizedMessage());
        }

        if (stopExchange) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        } else {
            ui.printInfo("Connection was established. Stop the encounter with the stopEncounter command.");
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Runs an encounter between two peers.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
