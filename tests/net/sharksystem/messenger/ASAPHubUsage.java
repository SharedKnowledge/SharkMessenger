package net.sharksystem.messenger;

import net.sharksystem.SharkException;
import net.sharksystem.asap.ASAPException;
import org.junit.Test;

import java.io.IOException;

public class ASAPHubUsage extends TestHelper {

    public ASAPHubUsage() {
        super(ASAPHubUsage.class.getSimpleName());
    }

    @Test
    public void usage() throws SharkException, ASAPException, IOException, InterruptedException {
        this.setUpScenario_1();

    }
}
