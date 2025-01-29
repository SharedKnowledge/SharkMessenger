package net.sharksystem.ui.messenger.cli.commands.hubmanagement;

import net.sharksystem.hub.hubside.ASAPTCPHub;
import net.sharksystem.ui.messenger.cli.SharkNetMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkNetMessengerUI;
import net.sharksystem.ui.messenger.cli.UICommand;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandBooleanArgument;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandIntegerArgument;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandQuestionnaire;

import java.io.IOException;
import java.util.List;

public class UICommandStartHub extends UICommand {
    private final UICommandIntegerArgument portArgument;
    private final UICommandBooleanArgument newConnectionArgument;
    private final UICommandIntegerArgument maxIdleInSecondsArgument;
    private int port;
    private boolean newConnection;
    private int maxIdleInSeconds;

    public UICommandStartHub(SharkNetMessengerApp sharkMessengerApp, SharkNetMessengerUI sharkMessengerUI,
                             String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.portArgument = new UICommandIntegerArgument(sharkMessengerApp);
        this.newConnectionArgument = new UICommandBooleanArgument(sharkMessengerApp);
        this.maxIdleInSecondsArgument = new UICommandIntegerArgument(sharkMessengerApp);
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    protected void execute() throws Exception {
        try {
            this.getSharkMessengerApp().startHub(this.port, this.newConnection, this.maxIdleInSeconds);
        } catch (IOException e) {
            this.printErrorMessage(e.getLocalizedMessage());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("start hub: ");
        sb.append("port (");
        sb.append(ASAPTCPHub.DEFAULT_PORT);
        sb.append(")");
        sb.append(", createNewConnection (true)");
        sb.append(", maxIdleInSeconds (");
        sb.append(ASAPTCPHub.DEFAULT_MAX_IDLE_CONNECTION_IN_SECONDS);
        sb.append(")");
        return sb.toString();
    }
    @Override
    protected boolean handleArguments(List<String> arguments) {
        // set defaults
        this.port = ASAPTCPHub.DEFAULT_PORT;
        this.newConnection = true;
        this.maxIdleInSeconds = ASAPTCPHub.DEFAULT_MAX_IDLE_CONNECTION_IN_SECONDS;

        // port
        if (arguments.size() > 0) {
            if(!this.portArgument.tryParse(arguments.get(0))) {
                this.getSharkMessengerApp().tellUIError("could not parse port:" + arguments.get(0));
                return false;
            } else {
                this.port = this.portArgument.getValue();
            }
        }

        // new connection
        if(arguments.size() > 1) {
            if(!this.newConnectionArgument.tryParse(arguments.get(1))) {
                this.getSharkMessengerApp().tellUIError("could not parse newConnection argument:" + arguments.get(1));
                return false;
            } else {
                this.newConnection = this.newConnectionArgument.getValue();
            }
        }

        // maxIdleInSeconds
        if(arguments.size() > 2) {
            if(!this.maxIdleInSecondsArgument.tryParse(arguments.get(2))) {
                this.getSharkMessengerApp().tellUIError("could not parse maxIdle (in sec) argument:" + arguments.get(2));
                return false;
            } else {
                this.maxIdleInSeconds = this.maxIdleInSecondsArgument.getValue();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("start hub on port: ");
        sb.append(this.port);
        sb.append(" | creates new connections: ");
        sb.append(this.newConnection);
        sb.append(" | connections max idle (in sec): ");
        sb.append(this.maxIdleInSeconds);

        this.getSharkMessengerApp().tellUI(sb.toString());
        return true;
    }
}