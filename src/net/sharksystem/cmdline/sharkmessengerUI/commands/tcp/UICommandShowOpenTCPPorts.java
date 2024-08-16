package net.sharksystem.cmdline.sharkmessengerUI.commands.tcp;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.commandarguments.UICommandQuestionnaire;

import java.util.Iterator;
import java.util.List;

public class UICommandShowOpenTCPPorts extends UICommand {
    public UICommandShowOpenTCPPorts(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                     String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected boolean handleArguments(List<String> arguments) {
        return true;
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    protected void execute() throws Exception {
        Iterator<Integer> portIter = this.getSharkMessengerApp().getOpenSockets();
        if(portIter == null || !portIter.hasNext()) {
            System.out.println("no open tcp ports");
            return;
        }

        StringBuilder sb = new StringBuilder();
        while(portIter.hasNext()) {
            sb.append(portIter.next());
            if(portIter.hasNext()) sb.append(", ");
        }
        System.out.println(sb);
    }

    @Override
    public String getDescription() {
        return "print open TCP ports";
    }
}