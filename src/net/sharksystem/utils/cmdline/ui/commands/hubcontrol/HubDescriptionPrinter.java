package net.sharksystem.utils.cmdline.ui.commands.hubcontrol;

import net.sharksystem.hub.ASAPHubException;
import net.sharksystem.hub.peerside.HubConnectorDescription;

import java.io.PrintStream;

public class HubDescriptionPrinter {
    static void print(PrintStream ps, HubConnectorDescription hcd) {
        try {
            CharSequence hostname = hcd.getHostName();
            int portNumber = hcd.getPortNumber();
            boolean canMultichannel = hcd.canMultiChannel();
            byte type = hcd.getType();

            // got all information - print it
            ps.print("host: ");
            ps.print(hostname);
            ps.print(" | ");
            ps.print("port: ");
            ps.print(portNumber);
            ps.print(" | ");
            ps.print("can multichannel: ");
            if(canMultichannel) ps.print("true"); else ps.print("false");
            ps.print(" | ");
            ps.print("type: ");
            ps.print(type);
        } catch (ASAPHubException e) {
            ps.println("problems receiving hub descriptions: " + e.getLocalizedMessage());
        }
    }
}
