package net.sharksystem.ui.messenger.cli.commands.messenger;

import net.sharksystem.ui.messenger.cli.commands.helper.AbstractCommandWithSingleInteger;
import net.sharksystem.app.messenger.SharkMessengerComponent;
import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;

/**
 * This command removes a channel from the peers known channels.
 */
public class UICommandRemoveChannelByIndex extends AbstractCommandWithSingleInteger {
    public UICommandRemoveChannelByIndex(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                         String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    public void execute() throws Exception {
        // we start with 1 in UI
        int channelIndex = this.getIntegerArgument() - 1;
        SharkMessengerComponent sharkMessengerComponent = this.getSharkMessengerApp().getSharkMessengerComponent();
        CharSequence uri = sharkMessengerComponent.getChannel(channelIndex).getURI();
        sharkMessengerComponent.removeChannel(uri);
        this.getSharkMessengerApp().tellUI("channel removed - index most probably changed");
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Removes a channel by index.");
        return sb.toString();
    }
}
