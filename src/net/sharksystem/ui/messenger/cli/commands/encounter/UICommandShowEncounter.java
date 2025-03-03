package net.sharksystem.ui.messenger.cli.commands.encounter;

import net.sharksystem.asap.ASAPEncounterManagerAdmin;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.utils.DateTimeHelper;
import net.sharksystem.ui.messenger.cli.SharkNetMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkNetMessengerUI;
import net.sharksystem.ui.messenger.cli.commands.helper.AbstractCommandNoParameter;

import java.util.Iterator;
import java.util.List;

public class UICommandShowEncounter extends AbstractCommandNoParameter {
    public UICommandShowEncounter(SharkNetMessengerApp sharkMessengerApp, SharkNetMessengerUI sharkMessengerUI,
                                  String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
    }

    @Override
    protected void execute() throws Exception {
        ASAPEncounterManagerAdmin encounterManagerAdmin = this.getSharkMessengerApp().getEncounterManagerAdmin();
        Iterator<CharSequence> connectPeersIter = encounterManagerAdmin.getConnectedPeerIDs().iterator();

        StringBuilder sb = new StringBuilder();
        if(connectPeersIter == null || !connectPeersIter.hasNext()) {
            sb.append("no encounter in the moment\n");
        } else {
            while (connectPeersIter.hasNext()) {
                sb.append(connectPeersIter.next());
                if (connectPeersIter.hasNext()) sb.append(", ");
            }
        }

        List<SharkNetMessengerApp.EncounterLog> encounterLogs =
                this.getSharkMessengerApp().getEncounterLogs();

        if(encounterLogs.size() < 1) {
            sb.append("no previous encounter\n");
        } else {
            sb.append("previous encounter:\n");
            for(SharkNetMessengerApp.EncounterLog encounterLog : encounterLogs) {
                sb.append("peer: ");
                sb.append(encounterLog.peerID);
                try {
                    CharSequence peerName = this.getSharkMessengerApp().getSharkPKIComponent()
                            .getPersonValuesByID(encounterLog.peerID).getName();
                    sb.append(" | name: ");
                    sb.append(peerName);
                }
                catch(ASAPException ae) {
                    // ignore - do not know that peer.
                }
                sb.append(" | connection: ");
                sb.append(encounterLog.encounterType.toString());
                sb.append(" | started: ");
                sb.append(DateTimeHelper.long2ExactTimeString(encounterLog.startTime));
                sb.append("\n");
            }
        }

        this.getSharkMessengerApp().tellUI(sb.toString());
    }

    @Override
    public String getDescription() {
        return "print open encounter";
    }
}
