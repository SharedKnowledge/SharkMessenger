# SharkMessenger

## SharkMessenger command-line interface

The main class for launching the CLI is located at `src/net/sharksystem/utils/cmdline/CLIMain.java`.

Executable jar can be found at `out/artifacts/SharkMessenger/SharkMessenger.jar`.

### Example usage:

Create the peers "Alice" and "Bob" and send a message to Bob:

```
mkPeer
Alice
mkPeer
Bob
mkCh
Alice
ch/test
testChannel
false
sendMsg
Alice
ch/test
false
false
HiBob
Bob
runEncounter
Alice
Bob
true
messages
Bob
ch/test
exit
```