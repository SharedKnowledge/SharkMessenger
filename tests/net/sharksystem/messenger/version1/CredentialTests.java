package net.sharksystem.messenger.version1;

public class CredentialTests {
    public void a2b2c() {
        /*
        step 1:
        A send signed message, opens (sendMessage signedA 1 true, openTCP 7777, lsMessages, showOpenTCPPorts) fails - cannot verify
        B connects to A, got message, cannot verify (connectTCP localhost 7777, wait 500, lsMessages) okay

        step 2:
        A asks for certificate (sendCredential) okay
        B accepts == issues (acceptCredential 1) okay

        step 3:
        A got certificate cert(B,A) (certBySubject Alice) okay
        B can verify signature, got certificate cert(B,A), know Alice (lsMessages, certByIssuer Bob, lsPersons) okay

        step 3: (init B - C  encounter)
        B opens (openTCP 7778)
        C connects (connectTCP localhost 7778)

        step 4: (C can verify with iA < 10)
        C can still not verify message (lsMessages) okay
        C got cert(B,A) (lsCerts) okay

        step 5: create cert(C,B)
        B asks clara for a certificate (sendCredential)
        C accepts == issues (acceptCredential 1)

        step 6: C can verify since there is a certification chain C -> B -> A
        C can verify message (lsMessages)

         */
    }

}
