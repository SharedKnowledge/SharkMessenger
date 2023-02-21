package net.sharksystem.messenger;

import net.sharksystem.SharkException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * For communication benefits, this test was separated in a new class
 */
public class SendMessageUncreatedChannelTest extends TestHelper {

    public SendMessageUncreatedChannelTest() {
        super(SendMessageUncreatedChannelTest.class.getSimpleName());
    }

    /**
     * Test if sending a message into a never created channel throws an exception
     * Maybe not throwing an exception here is the right/intended behavior
     */
    @Test
    public void sendMessageIntoUncreatedChannel() throws SharkException, IOException {
        this.setUpScenario_1();

        String someURI = "abc/123";

        //test if channel with this URI actually doesn't exist
        Assertions.assertThrows(SharkMessengerException.class, () -> this.aliceMessenger.getChannel(someURI));

        //some sort of Exception expected?
        Assertions.assertThrows(Exception.class, () ->
                this.aliceMessenger.sendSharkMessage("a".getBytes(), someURI, false, false));
    }

}
