package net.sharksystem.ui.messenger.cli.commands.testing;

import net.sharksystem.ui.messenger.cli.SharkNetMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkNetMessengerUI;
import net.sharksystem.ui.messenger.cli.commands.helper.AbstractCommandWithSingleString;

public class UICommandMarkStep extends AbstractCommandWithSingleString {
    public UICommandMarkStep(SharkNetMessengerApp sharkMessengerApp, SharkNetMessengerUI smUI, String echo, boolean b) {
        super(sharkMessengerApp, smUI, echo, b);
    }

    @Override
    protected void execute() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("***********************************************************************\n");
        sb.append("                   step: ");
        sb.append(this.getStringArgument());
        sb.append("\n");
        sb.append("***********************************************************************\n");
        this.getSharkMessengerApp().tellUI(sb.toString());
        int millis = 500;
        this.getSharkMessengerApp().tellUI("wait " + millis);
        Thread.sleep(millis);
    }

    @Override
    public String getDescription() {
        return "echo command";
    }
}
