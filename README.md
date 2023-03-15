# SharkMessenger

## SharkMessenger command-line interface

The main class for launching the CLI is located at `src/net/sharksystem/utils/cmdline/CLIMain.java`.

### example usage:

Create the peers "Alice" and "Bob" and send a message to Bob:

```
addPeer Alice
addPeer Bob
createChannel Alice ch/test TestChannel
sendMessage Alice ch/test false false HiBob
runEncounter Alice Bob true
getMessages Bob ch/test
```