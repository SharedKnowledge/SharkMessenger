package net.sharksystem.cmdline.sharkmessengerUI.commands.encounter;

import net.sharksystem.asap.ASAPEncounterManagerAdmin;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.commands.helper.AbstractCommandNoParameter;

import java.util.Iterator;
import java.util.List;

public class UICommandShowEncounter extends AbstractCommandNoParameter {
    public UICommandShowEncounter(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                                  String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected void execute() throws Exception {
        ASAPEncounterManagerAdmin encounterManagerAdmin = this.getSharkMessengerApp().getEncounterManagerAdmin();
        Iterator<CharSequence> connectPeersIter = encounterManagerAdmin.getConnectedPeerIDs().iterator();

        if(connectPeersIter == null || !connectPeersIter.hasNext()) {
            System.out.println("no encounter right now");
            return;
        }

        StringBuilder sb = new StringBuilder();
        while(connectPeersIter.hasNext()) {
            sb.append(connectPeersIter.next());
            if(connectPeersIter.hasNext()) sb.append(", ");
        }
        System.out.println(sb);
    }

    @Override
    public String getDescription() {
        return "print open encounter";
    }
}
