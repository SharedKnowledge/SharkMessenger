package net.sharksystem.cmdline.sharkmessengerUI.commands.simpleMessenger;

import net.sharksystem.asap.ASAPHop;
import net.sharksystem.cmdline.sharkmessengerUI.*;

import java.util.List;

public class UICommandGetMessageDetails extends UICommand {
    private final UICommandChannelArgument channel;
    private final UICommandIntegerArgument position;

    public UICommandGetMessageDetails(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                      String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.channel = new UICommandChannelArgument(sharkMessengerApp);
        this.position = new UICommandIntegerArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("Please set the channel uri: ", this.channel)
                .addQuestion("Please specify the position: ", this.position)
                .build();
    }

    @Override
    public void execute() throws Exception {
        int position = this.position.getValue();

        List<ASAPHop> hops = this.channel.getValue()
                        .getMessages()
                        .getSharkMessage(position, true)
                        .getASAPHopsList();


        StringBuilder sb = new StringBuilder();
        hops.forEach(hop -> {
            sb.append(hop.sender());
            if (hop != hops.get(hops.size() - 1)) {
                sb.append(" -> ");
            }
        });
        this.getPrintStream().println(sb.toString());
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Returns the hops of a message.");
        return sb.toString();
    }

    /**
     * @param arguments in following order:
     * <ol>
     *  <li>channelURI - String</li>
     *  <li>position - int</li>
     * </ol>
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if(arguments.size() < 2) {
            return false;
        }
        boolean isParsable = channel.tryParse(arguments.get(0)) 
                && position.tryParse(arguments.get(1));
                
        return isParsable;
    }

}
