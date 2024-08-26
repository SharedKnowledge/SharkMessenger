package net.sharksystem.messenger.cli.commands.encounter;

import net.sharksystem.asap.ASAPEncounterManagerAdmin;
import net.sharksystem.messenger.cli.SharkMessengerApp;
import net.sharksystem.messenger.cli.SharkMessengerUI;
import net.sharksystem.messenger.cli.commands.helper.AbstractCommandNoParameter;

import java.util.Iterator;

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
