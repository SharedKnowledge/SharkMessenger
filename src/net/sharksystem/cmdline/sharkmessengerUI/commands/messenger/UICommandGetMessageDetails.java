package net.sharksystem.cmdline.sharkmessengerUI.commands.messenger;

import net.sharksystem.asap.ASAPHop;
import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;

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

}
