package net.sharksystem.utils.cmdline;

import java.io.*;
import java.util.*;

public class CmdLineUI {

    //directly copied from SharkMessengerComponent interface
    //TODO: add/remove/adapt commands as needed
    public static final String GET_ALL_CHANNELS = "getallchannels";
    public static final String GET_CHANNEL = "getchannels";
    public static final String CREATE_CHANNEL  = "mkchannel";
    public static final String REMOVE_CHANNEL  = "rmchannels";
    public static final String REMOVE_ALL_CHANNELS  = "rmallchannels";
    public static final String SEND_MESSAGE  = "sendmessage";
    public static final String EXIT = "exit";


    private final PrintStream standardOut;
    private final PrintStream standardErr;
    private final BufferedReader bufferedReader;

    private final HashMap<String, String> commands;



    public static void main(String[] args) {
        CmdLineUI userCmd = new CmdLineUI(System.in, System.err, System.out);
        //TODO: implement processing of main parameter input array


        userCmd.printWelcome();
        userCmd.printUsage();
        userCmd.runCommandLoop();

    }

    /**
     * Converts an array of parameters into a hashmap of key value pairs
     * @param args parameter input array
     * @param valueRequired whether every key needs to have a value
     * @param helpMessage message when error occurs
     * @return all parameters as hashmap of key value pairs
     */
    public static HashMap<String, String> parametersToMap(String[] args, boolean valueRequired, String helpMessage) {
        if(valueRequired && args.length % 2 != 0) {
            System.err.println("malformed parameter list: each parameter needs a value. ");
            System.err.println(helpMessage);
            return null;
        }

        HashMap<String, String> argumentMap = new HashMap<>();

        int i = 0;
        while(i < args.length) {
            // key is followed by value. Key starts with -
            if(!args[i].startsWith("-")) {
                /* found parameter that does not start with '-'
                maybe shell parameters. Leave it alone. We are done here
                */
                return argumentMap;
            }

            // value can be empty
            if(args.length > i+1 && !args[i+1].startsWith("-")) {
                // it is a value
                argumentMap.put(args[i], args[i+1]);
                i += 2;
            } else {
                // no value - next parameter
                argumentMap.put(args[i], null);
                i += 1;
            }
        }

        return argumentMap;
    }


    /**
     * Constructor for the UI
     * @param in input stream from which the UI should read user input
     * @param err print stream for occurring errors
     * @param out print stream to write to
     */
    public CmdLineUI(InputStream in,PrintStream err, PrintStream out) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(in));
        this.standardErr = err;
        this.standardOut = out;

        //maybe not the best solution
        //better overview over all commands and their description as if this was all placed into the printUsage method

        this.commands = new HashMap<>();
        this.commands.put(GET_ALL_CHANNELS   , "prints a list of all channels"             );
        this.commands.put(GET_CHANNEL        , "prints information about specified channel");
        this.commands.put(CREATE_CHANNEL     , "create a new closed channel"               );
        this.commands.put(REMOVE_ALL_CHANNELS, "removes all channels"                      );
        this.commands.put(REMOVE_CHANNEL     , "removes a specified channel"               );
        this.commands.put(SEND_MESSAGE       , "sends a message"                           );
        this.commands.put(EXIT               , "exit"                                      );
    }

    public void printWelcome() {
        this.standardOut.println("Welcome to SharkMessenger version 0.1");
    }

    /**
     * Prints all valid commands with their description
     */
    public void printUsage() {
        StringBuilder sb = new StringBuilder();

        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("COMMANDS:");
        sb.append(System.lineSeparator());

        for(String commandIdentifier : this.commands.keySet()) {
            sb.append(commandIdentifier);
            //TODO: print right amount of tabs to aline all descriptions
            sb.append("\t\t\t");
            sb.append(this.commands.get(commandIdentifier));
            sb.append(System.lineSeparator());
        }

        this.standardOut.println(sb);
    }


    /**
     * Starting the loop which acts like a listener to user input
     */
    public void runCommandLoop() {
        boolean again = true;
        while(again) {
            try {
                String userInputString = this.bufferedReader.readLine();

                if(userInputString != null) {
                    List<String> cmd = optimizeUserInputString(userInputString);

                    //the reason for removing the first argument (=command identifier) is that this here is the only
                    //  place where it's needed. A method performing the action of a command only needs the arguments
                    //  specified and not the command identifier
                    String commandIdentifier = cmd.remove(0);

                    switch (commandIdentifier) {
                        case GET_ALL_CHANNELS :
                            doPrintAllChannels();
                            break;

                        case GET_CHANNEL :
                            doPrintChannel();
                            break;

                        case CREATE_CHANNEL :
                            doCreateChannel();
                            break;

                        case REMOVE_ALL_CHANNELS :
                            doRemoveAllChannels();
                            break;

                        case REMOVE_CHANNEL :
                            doRemoveChannel();
                            break;

                        case SEND_MESSAGE :
                            doSendMessage();
                            break;

                        case EXIT :
                            doExit();
                            again = false;
                            break;

                        default :
                            this.standardErr.println("Error: unknown command: " + commandIdentifier);
                            break;
                    }
                }

            } catch (IOException e) {
                this.standardErr.println("Error: cannot read from input stream");
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                    Methods                                                     //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void doPrintAllChannels() {
    }

    private void doPrintChannel() {
    }

    private void doCreateChannel() {
    }

    private void doRemoveAllChannels() {
    }

    private void doRemoveChannel() {
    }

    private void doSendMessage() {
    }

    private void doExit() {
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                    Helpers                                                     //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Converts a string representing a command input by the user into a list of all command arguments.
     * All arguments are freed up from any spaces. All elements of the list is in lower case.
     * Example: (upper cases, too many spaces)
     * > gIt   hElp
     * > {"git", "help"}
     *
     * @param input the string inputted by the user
     * @return a list of all command arguments
     */
    private List<String> optimizeUserInputString(String input) {
        List<String> cmd = new ArrayList<>();
        final String space = " ";

        String[] unfinishedCmd = input.split(space);
        for (String attribute : unfinishedCmd) {
            attribute = attribute.toLowerCase();
            attribute = attribute.trim();

            if (!attribute.equals(space)) {
                cmd.add(attribute);
            }
        }

        return cmd;
    }

}
