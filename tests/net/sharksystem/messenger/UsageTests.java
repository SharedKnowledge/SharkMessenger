package net.sharksystem.messenger;

import net.sharksystem.*;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import org.junit.Test;

import static net.sharksystem.messenger.TestConstants.*;


public class UsageTests {
    public static final String SUB_ROOT_DIRECTORY = TestConstants.ROOT_DIRECTORY
            + UsageTests.class.getSimpleName() + "/";
    public static final String MESSAGE = "Hi";
    public static final String URI = "sn2://all";

    public static final String ALICE_FOLDER = SUB_ROOT_DIRECTORY + ALICE_ID;

    @Test
    public void usage1() throws SharkException {
        SharkTestPeerFS alicePeer = new SharkTestPeerFS(ALICE_ID, ALICE_FOLDER);

        // certificate
        SharkPKIComponentFactory certFactory = new SharkPKIComponentFactory();
        alicePeer.addComponent(certFactory, SharkPKIComponent.class);

        // this component
        SharkMessengerComponentFactory messengerFactory =
                new SharkMessengerComponentFactory(
                        (SharkPKIComponent) alicePeer.getComponent(SharkPKIComponent.class)
                );

        alicePeer.addComponent(messengerFactory, SharkMessengerComponent.class);

        SharkMessengerComponent aliceMessenger =
                (SharkMessengerComponent) alicePeer.getComponent(SharkMessengerComponent.class);



    }
}
