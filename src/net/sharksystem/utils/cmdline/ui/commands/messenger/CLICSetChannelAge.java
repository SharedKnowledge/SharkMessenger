package net.sharksystem.utils.cmdline.ui.commands.messenger;

import net.sharksystem.messenger.SharkCommunicationAge;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.ui.*;

public class CLICSetChannelAge extends CLICommand {
    private final CLICChannelArgument channel;
    private final CLICStringArgument channelAge;

    public CLICSetChannelAge(SharkMessengerApp sharkMessengerApp, String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, identifier, rememberCommand);
        this.channel = new CLICChannelArgument();
        channelAge = new CLICStringArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder()
                .addQuestion("Channel URI: ", this.channel)
                .addQuestion("Channel age: ", this.channelAge)
                .build();
    }

    @Override
    public void execute() throws Exception {
        SharkCommunicationAge age = SharkCommunicationAge.valueOf(this.channelAge.getValue());
        this.channel.getValue().setAge(age);
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Changes channel age.");
        return sb.toString();
    }

}