package net.sharksystem.utils.cmdline.ui.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.ui.*;

import java.io.IOException;

public class CLICRemoveChannel extends CLICommand {

    private final CLICChannelArgument channel;


    public CLICRemoveChannel(SharkMessengerApp sharkMessengerApp, String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, identifier, rememberCommand);
        this.channel = new CLICChannelArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder()
                .addQuestion("Channel URI: ", this.channel)
                .build();
    }

    @Override
    public void execute() throws Exception {
        try {
            SharkMessengerComponent peerMessenger = model.getMessengerComponent();
            peerMessenger.removeChannel(this.channel.getValue().getURI());

        } catch (SharkException | IOException e) {
            ui.printError(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Removes a channel.");
        return sb.toString();
    }

}
