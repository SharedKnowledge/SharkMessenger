package net.sharksystem.messenger;

import net.sharksystem.SharkException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * For communication benefits, this test was separated in a new class
 */
public class ASAPRoutingAllowedTest extends TestHelper {

    public ASAPRoutingAllowedTest() {
        super(ASAPRoutingAllowedTest.class.getSimpleName());
    }

    /**
     * Tests if the routingAllowed attribute of a peer is changed by calling the therefor intended method
     * Test in wrong project, as it tests functionality from library ASAPJava
     *
     * @throws SharkException failure should not be a result of this test
     * @throws IOException failure should not be a result of this test
     */
    @Test
    public void routingAllowedChangesRoutingRules() throws SharkException, IOException {
        this.setUpScenario_1();

        this.bobPeer.getASAPTestPeerFS().setASAPRoutingAllowed("shark/messenger",false);
        Assertions.assertFalse(this.bobPeer.getASAPTestPeerFS().isASAPRoutingAllowed("shark/messenger"));
    }
}
