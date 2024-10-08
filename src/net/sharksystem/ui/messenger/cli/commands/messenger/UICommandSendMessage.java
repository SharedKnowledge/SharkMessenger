package net.sharksystem.ui.messenger.cli.commands.messenger;

import net.sharksystem.asap.utils.ASAPSerialization;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandQuestionnaire;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandQuestionnaireBuilder;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandStringArgument;
import net.sharksystem.app.messenger.SharkMessengerComponent;
import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;
import net.sharksystem.ui.messenger.cli.UICommand;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class UICommandSendMessage extends UICommand {
    private final UICommandStringArgument message;

    public UICommandSendMessage(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        this.message = new UICommandStringArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder()
                .addQuestion("message: ", this.message)
                .build();
    }

    @Override
    public void execute() throws Exception {
        SharkMessengerComponent messengerComponent = this.getSharkMessengerApp().getSharkMessengerComponent();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ASAPSerialization.writeCharSequenceParameter(this.message.getValue(), baos);
        messengerComponent.sendSharkMessage(baos.toByteArray(),
                SharkMessengerComponent.UNIVERSAL_CHANNEL_URI,false);
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Broadcast messages in Shark universal channel. (" + SharkMessengerComponent.UNIVERSAL_CHANNEL_URI + ")");
        return sb.toString();
    }

    /**
     * @param arguments in following order:
     * <ol>
     *  <li>subject - peerID</li>
     * </ol>
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if(arguments.size() < 1) {
            this.getSharkMessengerApp().tellUI("message missing");
            return false;
        }

        boolean isParsable = message.tryParse(arguments.get(0));
        return isParsable;
    }
}
