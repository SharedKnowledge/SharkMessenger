package net.sharksystem.utils.cmdline.ui.commands.messenger;

import net.sharksystem.messenger.SharkMessengerChannel;
import net.sharksystem.messenger.SharkMessengerComponent;
import net.sharksystem.messenger.SharkMessengerException;
import net.sharksystem.utils.cmdline.SharkMessengerApp;
import net.sharksystem.utils.cmdline.SharkMessengerUI;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.ui.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.ui.CLICommand;

import java.io.IOException;
import java.util.List;

public class CLICListChannels extends CLICommand {
    /**
     * Creates a command object.
     *
     * @param sharkMessengerApp
     * @param sharkMessengerUI
     * @param identifier        The identifier of the command.
     * @param rememberCommand   If the command should be saved in the history log.
     */
    public CLICListChannels(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                            String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().build();
    }

    @Override
    protected void execute() throws Exception {
        try {
            SharkMessengerComponent messengerComponent = this.getSharkMessengerApp().getMessengerComponent();
            ChannelPrinter.printChannelDescriptions(this.getPrintStream(), messengerComponent, false);
        } catch (SharkMessengerException | IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        return "list existing channels";
    }
}
