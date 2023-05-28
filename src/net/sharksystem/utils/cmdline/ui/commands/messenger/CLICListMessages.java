package net.sharksystem.utils.cmdline.ui.commands.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.messenger.SharkMessage;
import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerException;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CLICListMessages extends CLICProduceChannelListBefore {
    private final CLICIntegerArgument channelIndex;

    public CLICListMessages(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                            String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.channelIndex = new CLICIntegerArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder()
                .addQuestion("Index target channel (0..n): ", this.channelIndex)
                .build();
    }

    @Override
    public void execute() throws Exception {
        try {
            SharkMessageList messages =
                this.getSharkMessengerApp().getMessengerComponent().getChannel(
                        this.channelIndex.getValue()).getMessages();

            ChannelPrinter.printMessages(this.getPrintStream(), messages);
        } catch (SharkException | IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        return "Returns one or more messages a peer received.";
    }
}
