package net.sharksystem.utils.cmdline.control.commands.messenger;

import net.sharksystem.messenger.SharkCommunicationAge;
import net.sharksystem.utils.cmdline.control.*;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

public class CLICSetChannelAge extends CLICommand {

    private final CLICSharkPeerArgument peer;
    private final CLICChannelArgument channel;
    private final CLICStringArgument channelAge;

    public CLICSetChannelAge(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.peer = new CLICSharkPeerArgument();
        this.channel = new CLICChannelArgument(this.peer);
        channelAge = new CLICStringArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("Peer Name: ", this.peer).
                addQuestion("Channel URI: ", this.channel).
                addQuestion("Channel age: ", this.channelAge).
                build();
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model) throws Exception {
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