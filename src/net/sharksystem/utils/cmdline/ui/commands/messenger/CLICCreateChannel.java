package net.sharksystem.utils.cmdline.ui.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeerFS;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerException;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.CLICommand;
import net.sharksystem.utils.cmdline.ui.CLICBooleanArgument;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.ui.CLICStringArgument;

import java.io.IOException;

public class CLICCreateChannel extends CLICommand {
    private final CLICStringArgument channelUri;
    private final CLICStringArgument channelName;
    private final CLICBooleanArgument channelMustNotExist;

    public CLICCreateChannel(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                             String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.channelUri = new CLICStringArgument();
        this.channelName = new CLICStringArgument();
        this.channelMustNotExist = new CLICBooleanArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder()
                .addQuestion("Please input the channel uri: ", this.channelUri)
                .addQuestion("Please set the channel name: ", this.channelName)
                .build();
    }

    @Override
    public void execute() {
        String channelURI = this.channelUri.getValue();
        String channelName = this.channelName.getValue();

        try {
            this.getSharkMessengerApp().getMessengerComponent().createChannel(channelURI, channelName, true);
        } catch (SharkMessengerException | IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creates a new channel.");
        return sb.toString();
    }

}
