package net.sharksystem.utils.cmdline.control.commands.messenger;

import net.sharksystem.messenger.SharkCommunicationAge;
import net.sharksystem.utils.cmdline.control.*;

public class CLICSetChannelAge extends CLICommand {

    private final CLICStringArgument peerName;
    private final CLICChannelArgument channel;
    private final CLICStringArgument channelAge;

    public CLICSetChannelAge(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.peerName = new CLICStringArgument();
        this.channel = new CLICChannelArgument(this.peerName);
        channelAge = new CLICStringArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Peer Name: ", this.peerName).
                addQuestion("Channel URI: ", this.channel).
                addQuestion("Channel age: ", this.channelAge).
                build();
    }

    @Override
    public void execute() throws Exception {
        SharkCommunicationAge age = SharkCommunicationAge.valueOf(this.channelAge.getValue());
        this.channel.getValue().setAge(age);
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Changes the channel age.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}