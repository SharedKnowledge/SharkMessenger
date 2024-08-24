package net.sharksystem.messenger.version1;

public class MessageRoutingTests {
    public void a2b2c() {
        /* A messages, opens, B connects A, opens
        A: sendMessage testMessageFromA, wait 100, openTCP 7777, showOpenTCPPorts
        B: connectTCP localhost 7777, wait 100, openTCP 7778, lsMessages
        C: connectTCP localhost 7777, wait 100, lsMessages

        Check: Message is in A and B
        works
         */
    }
}
