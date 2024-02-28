package net.sharksystem.cmdline.sharkmessengerUI.commands.messenger;

import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.*;

/**
 * This command removes a channel from the peers known channels.
 */
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

    /**
     * @param arguments in following order:
     * <ol>
     *  <li>channel - channelURI</li>
     * </ol>
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if(arguments.size() < 1) {
            return false;
        }

        boolean isParsable = channel.tryParse(arguments.get(0));
        return isParsable;
    }
}
