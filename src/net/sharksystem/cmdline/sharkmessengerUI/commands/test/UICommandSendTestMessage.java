package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import java.util.ArrayList;
import java.util.List;

import net.sharksystem.cmdline.sharkmessengerUI.*;
import net.sharksystem.cmdline.sharkmessengerUI.commands.messenger.UICommandSendMessage;
import net.sharksystem.messenger.SharkMessengerComponent;

/**
 * This command is for batch test purposes. It adds a test run specific ID to each message and has two additional
 * parameters:
 * repetitions - defines how often the message will be sent
 * delayInMillis - defines how much time will pass until a message is sent
 */
public class UICommandSendTestMessage extends UICommand {
    private final UICommandIntegerArgument repetitions;
    private final UICommandLongArgument delayInMillis;
    private final UICommandIntegerArgument channelIndex;
    private final UICommandBooleanArgument sign;
    private final UICommandBooleanArgument encrypt;
    private final UICommandStringArgument message;
    private final UICommandStringArgument receivers;

    public UICommandSendTestMessage(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
            String identifier, boolean rememberCommand) {

        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);

        // additional parameters for tests
        this.repetitions = new UICommandIntegerArgument(sharkMessengerApp);
        this.delayInMillis = new UICommandLongArgument(sharkMessengerApp);
        // standard send message command parameters
        this.channelIndex = new UICommandIntegerArgument(sharkMessengerApp);
        this.sign = new UICommandBooleanArgument(sharkMessengerApp);
        this.encrypt = new UICommandBooleanArgument(sharkMessengerApp);
        this.message = new UICommandStringArgument(sharkMessengerApp);
        this.receivers = new UICommandStringArgument(sharkMessengerApp);
        // allow broadcast msg
        this.receivers.setEmptyStringAllowed(true);
    }

    /**
     * Prepares the command with parsing the arguments.
     * @param arguments in following order:
     *                  0 repetitions
     *                  1 delay
     *                  2 channel index
     *                  3 sign
     *                  4 encrypt
     *                  5 message
     *                  6 receivers
     * @return True if the arguments are parsable.
     */
    @Override
    protected boolean handleArguments(List<String> arguments) {
        if (arguments.size() < 7) {
            return false;
        }

        boolean isParsable = repetitions.tryParse(arguments.get(0))
                && delayInMillis.tryParse(arguments.get(1))
                && channelIndex.tryParse(arguments.get(2))
                && sign.tryParse(arguments.get(3))
                && encrypt.tryParse(arguments.get(4))
                && message.tryParse(arguments.get(5))
                && receivers.tryParse(arguments.get(6));

        if (repetitions.getValue() < 0) {
            repetitions.setValue(0);
        }

        if (delayInMillis.getValue() < 0) {
            delayInMillis.setValue(0L);
        }

        return isParsable;
    }

    @Override
    protected UICommandQuestionnaire specifyCommandStructure() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'specifyCommandStructure'");
    }

    @Override
    protected void execute() throws Exception {
        UICommand sendCommand = new UICommandSendMessage(getSharkMessengerApp(),
                getSharkMessengerUI(), getIdentifier(), false);

        // messageCounter provides message IDs
        SentMessageCounter messageCounter = SentMessageCounter.getInstance();

        // prepare argument list for sendMessage command
        List<String> arguments = new ArrayList<>();
        arguments.add(this.channelIndex.getValue().toString());
        arguments.add(this.sign.getValue().toString());
        arguments.add(this.encrypt.getValue().toString());
        arguments.add(this.receivers.getValue());

        // information for the messageCounter
        SharkMessengerComponent messenger = this.getSharkMessengerApp().getMessengerComponent();
        String channelUri = messenger.getChannel(this.channelIndex.getValue()).getURI().toString();
        String content = this.message.getValue();
        String receivers = this.receivers.getValue();

        for (int i = 0; i < this.repetitions.getValue(); i++) {
            Thread.sleep(this.delayInMillis.getValue());

            // add ID to the message and put it into the argument list for the sendMessage command
            int msgID = messageCounter.sendNextMessage(receivers, channelUri, content);
            String msg = msgID
                    + System.lineSeparator()
                    + this.message.getValue();
            arguments.add(3, msg);

            sendCommand.initializeExecution(arguments);
        }
    }

    @Override
    public String getDescription() {
        return "Send a specified amount of messages";
    }

}
