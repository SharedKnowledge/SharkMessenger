package net.sharksystem.messenger;

import net.sharksystem.SharkComponent;
import net.sharksystem.SharkException;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.hub.peerside.TCPHubConnectorDescription;
import org.junit.Test;

import java.io.IOException;

import static net.sharksystem.messenger.TestConstants.ALICE_ID;

public class HubDescriptionTests {
    @Test
    public void test1() throws SharkException, IOException {
        String aliceFolderName = TestConstants.ROOT_DIRECTORY + "/hubdescriptions";
        SharkTestPeerFS.removeFolder(aliceFolderName);
        SharkTestPeerFS alicePeer = new SharkTestPeerFS(ALICE_ID, aliceFolderName);
        TestHelper.setupComponent(alicePeer);

        alicePeer.start();

        SharkMessengerComponent aliceComponent =
                (SharkMessengerComponent) alicePeer.getComponent(SharkMessengerComponent.class);

        aliceComponent.addHubDescription(new TCPHubConnectorDescription("exampleHost_A", 1234));
        aliceComponent.addHubDescription(new TCPHubConnectorDescription("exampleHost_B", 1235));
        aliceComponent.addHubDescription(new TCPHubConnectorDescription("exampleHost_C", 1265));

        // relaunch
        alicePeer = new SharkTestPeerFS(ALICE_ID, aliceFolderName);
        TestHelper.setupComponent(alicePeer);

        alicePeer.start();

        aliceComponent = (SharkMessengerComponent) alicePeer.getComponent(SharkMessengerComponent.class);
        aliceComponent.getHubDescriptions(0);
        aliceComponent.getHubDescriptions(1);
        aliceComponent.getHubDescriptions(2);

    }
}
