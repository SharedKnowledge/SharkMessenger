package net.sharksystem.cmdline.sharkmessengerUI.commands.messenger;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.messenger.SharkCommunicationAge;

public class UICommandSetChannelAge extends UICommand {
    private final UICommandChannelArgument channel;
    private final UICommandStringArgument channelAge;

    public UICommandSetChannelAge(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                  String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.channel = new UICommandChannelArgument(sharkMessengerApp);
        channelAge = new UICommandStringArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
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
    
    /**
     * Arguments needed in this order: 
     * <p>
     * channel as UICommandChannelArgument
     * <p>
     * channelAge as UICommandStringArgument
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if(arguments.size() < 2) {
            return false;
        }
        boolean isParsable = channel.tryParse(arguments.get(0)) && channelAge.tryParse(arguments.get(1));
        return isParsable;
    }

}