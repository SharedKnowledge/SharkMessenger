package net.sharksystem.cmdline.sharkmessengerUI.commands.messenger;

import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;

public class UICommandRemoveChannel extends UICommand {

    private final UICommandChannelArgument channel;


    public UICommandRemoveChannel(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                  String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.channel = new UICommandChannelArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("Channel URI: ", this.channel)
                .build();
    }

    @Override
    public void execute() throws Exception {
        this.printTODOReimplement();
        /*
        try {
            SharkMessengerComponent peerMessenger = model.getMessengerComponent();
            peerMessenger.removeChannel(this.channel.getValue().getURI());

        } catch (SharkException | IOException e) {
            ui.printError(e.getLocalizedMessage());
        }
         */
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Removes a channel.");
        return sb.toString();
    }

}
