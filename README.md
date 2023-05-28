# SharkMessenger

## SharkMessenger command-line interface
Shark Messenger command line interface can be launced by using
java -jar SharkMessengerUI.jar

This jar can be produced by running the makeSharkMessengerUI in src root folder. Have fun.

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