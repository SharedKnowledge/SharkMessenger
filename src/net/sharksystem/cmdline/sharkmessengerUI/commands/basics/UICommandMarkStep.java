package net.sharksystem.cmdline.sharkmessengerUI.commands.basics;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandWithSingleString;

public class UICommandMarkStep extends AbstractCommandWithSingleString {
    public UICommandMarkStep(SharkMessengerApp sharkMessengerApp, SharkMessengerUI smUI, String echo, boolean b) {
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
