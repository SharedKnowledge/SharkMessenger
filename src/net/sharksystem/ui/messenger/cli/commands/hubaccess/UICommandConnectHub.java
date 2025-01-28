package net.sharksystem.ui.messenger.cli.commands.hubaccess;

import net.sharksystem.SharkException;
import net.sharksystem.hub.hubside.ASAPTCPHub;
import net.sharksystem.ui.messenger.cli.SharkMessengerApp;
import net.sharksystem.ui.messenger.cli.SharkMessengerUI;
import net.sharksystem.ui.messenger.cli.UICommand;
import net.sharksystem.hub.peerside.HubConnectorDescription;
import net.sharksystem.hub.peerside.TCPHubConnectorDescriptionImpl;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandBooleanArgument;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandIntegerArgument;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandQuestionnaire;
import net.sharksystem.ui.messenger.cli.commandarguments.UICommandStringArgument;

import java.io.PrintStream;
import java.util.List;

public class UICommandConnectHub extends UICommand {
    private final UICommandStringArgument hubHostArgument;
    private final UICommandIntegerArgument hubPortNumberArgument;
    private final UICommandBooleanArgument createNewChannelArgument;
    private String hubHost;
    private int hubPort;
    private boolean createNewChannel;

    public UICommandConnectHub(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                               String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        this.hubHostArgument = new UICommandStringArgument(sharkMessengerApp);
        this.hubPortNumberArgument = new UICommandIntegerArgument(sharkMessengerApp);
        this.createNewChannelArgument = new UICommandBooleanArgument(sharkMessengerApp);
    }
    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    protected void execute() throws Exception {
        HubConnectorDescription hubDescription =
                new TCPHubConnectorDescriptionImpl(this.hubHost, this.hubPort, this.createNewChannel);

        this.getSharkMessengerApp().getHubConnectionManager().connectHub(hubDescription);
        this.getSharkMessengerApp().tellUI("try to connect");
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    UICommandConnectHub.this.getSharkMessengerApp().tellUI("connected hubs:\n");
                    HubDescriptionPrinter.printConnectedHubs(
                            new PrintStream(UICommandConnectHub.this.getSharkMessengerApp().getOutStream()),
                            UICommandConnectHub.this.getSharkMessengerApp().getHubConnectionManager()
                            );
                    UICommandConnectHub.this.getSharkMessengerApp().tellUI("\n");

                } catch (InterruptedException e) {
                    // ignore - wont happen
                } catch (SharkException e) {
                    UICommandConnectHub.this.getSharkMessengerApp().tellUIError("problem: " + e.getLocalizedMessage());
                }

            }
        }).start();
         */
    }

    @Override
    public String getDescription() {
        return "connects to a hub: " +
                "hostname (localhost), " +
                "port (" + ASAPTCPHub.DEFAULT_PORT +
                "), createNewConnection (yes)";
    }

    @Override
    protected boolean handleArguments(List<String> arguments) {
        // set defaults
        this.hubHost = "localhost";
        this.hubPort = ASAPTCPHub.DEFAULT_PORT;
        this.createNewChannel = true;

        // host
        if (arguments.size() > 0) {
            if (!this.hubHostArgument.tryParse(arguments.get(0))) {
                this.getSharkMessengerApp().tellUIError("could not parse hostname:" + arguments.get(0));
                return false;
            } else {
                this.hubHost = this.hubHostArgument.getValue();
            }
        }

        // host
        if (arguments.size() > 1) {
            if (!this.hubPortNumberArgument.tryParse(arguments.get(1))) {
                this.getSharkMessengerApp().tellUIError("could not parse port:" + arguments.get(1));
                return false;
            } else {
                this.hubPort = this.hubPortNumberArgument.getValue();
            }
        }

        // newChannel
        if (arguments.size() > 2) {
            if (!this.createNewChannelArgument.tryParse(arguments.get(2))) {
                this.getSharkMessengerApp().tellUIError("could not new channel option (true/false):" + arguments.get(2));
                return false;
            } else {
                this.createNewChannel = this.createNewChannelArgument.getValue();
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("connect to hub on ");
        sb.append(this.hubHost);
        sb.append(", port ");
        sb.append(this.hubPort);
        sb.append(", createNewChannel: ");
        sb.append(this.createNewChannel);
        this.getSharkMessengerApp().tellUI(sb.toString());

        return true;
    }
}
