package net.sharksystem.cmdline.sharkmessengerUI.commands.test;

import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.messenger.SharkMessagesReceivedListener;
import net.sharksystem.messenger.SharkMessage;
import net.sharksystem.messenger.SharkMessageList;
import net.sharksystem.messenger.SharkMessengerException;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TestMessageReceivedListener implements SharkMessagesReceivedListener {
    private final SharkMessengerApp sharkMessengerApp;
    private static TestMessageReceivedListener instance;
    private final Map<CharSequence, Map<Integer, Long>> receivedMessages;

    public TestMessageReceivedListener(SharkMessengerApp sharkMessengerApp) {
        this.sharkMessengerApp = sharkMessengerApp;
        this.receivedMessages = new HashMap<>();
    }

    public static TestMessageReceivedListener getInstance (){
        if (instance == null){
            System.out.println("Not initialized!");
        }
        return instance;
    }

    @Override
    public void sharkMessagesReceived(CharSequence uri) {
        int messageCounter = 0;
        if (receivedMessages.containsKey(uri)){
            messageCounter = receivedMessages.get(uri).size();
        }else{
            receivedMessages.put(uri, new HashMap<>());
        }
        try {
            SharkMessageList messages = this.sharkMessengerApp.getMessengerComponent().getChannel(uri).getMessages();
            //int newMessagesCounter = messages.size()- messageCounter;
            long currentTime = System.currentTimeMillis();
            for (int i = messages.size()-1; i >= messageCounter; i--){
                SharkMessage message = messages.getSharkMessage(i, false);
                int id =  Integer.parseInt(getIDFromContent(message.getContent()));
                receivedMessages.get(uri).put(id, currentTime);
            }

            // save received msg with timestamp
        } catch (SharkMessengerException | IOException e) {
            System.out.println("TODO: exception in MessageReceivedListener:" + e.getLocalizedMessage());
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ASAPSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getIDFromContent(byte[] content) {
       String contentString = new String(content, StandardCharsets.UTF_8);
       return contentString.split(System.lineSeparator())[0];
    }
    public long getReceivedTime(CharSequence uri, int messageID){
        return receivedMessages.get(uri).get(messageID);
    }
    public int getMessageCount(CharSequence uri){
        if(!receivedMessages.containsKey(uri)){
            return 0;
        }
        return receivedMessages.get(uri).size();
    }
    public int getMessageCount(){
        return receivedMessages.entrySet().stream()
        .map(x-> x.getValue().size())
        .reduce(0, Integer::sum);
    }
}
