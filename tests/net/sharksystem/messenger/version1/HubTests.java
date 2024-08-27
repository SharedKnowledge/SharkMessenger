package net.sharksystem.messenger.version1;

public class HubTests {
    public void a2b2c() {
        /* H opens hub, all connect; C sends message, all receive
        step 1:
        H: startHub 8888

        step 2:
        A: connectHub localhost 8888
        B: connectHub localhost 8888
        C: connectHub localhost 8888

        step 3
        C: sendMessage HiThere

        step 4:
        A: lsMessages
        B: lsMessages
        C: lsMessages
         */
    }

}
