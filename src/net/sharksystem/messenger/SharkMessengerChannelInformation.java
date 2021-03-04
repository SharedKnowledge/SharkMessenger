package net.sharksystem.messenger;

import net.sharksystem.utils.Log;

public class SharkMessengerChannelInformation {
    public final CharSequence uri;
    public final CharSequence name;
    private boolean internetAge = false;
    private boolean bronzeAge = true;
    private boolean stoneAge = false;

    SharkMessengerChannelInformation(CharSequence uri, CharSequence name) {
        this(uri, name, SharkMessengerComponent.DEFAULT_AGE);
    }

    SharkMessengerChannelInformation(CharSequence uri, CharSequence name, String communicationBehaviour) {
        this.uri = uri;
        this.name = name;

        switch (communicationBehaviour) {
            case SharkMessengerComponent.SHARK_MESSENGER_STONE_AGE_MODE: this.stoneAge = true; break;
            case SharkMessengerComponent.SHARK_MESSENGER_BRONZE_AGE_MODE: bronzeAge = true; break;
            case SharkMessengerComponent.SHARK_MESSENGER_INTERNET_AGE_MODE: internetAge = true; break;
            default:
                Log.writeLog(this, "unknown communication behaviour: " + communicationBehaviour);
        }
    }

    public boolean isStoneAge() { return this.stoneAge; }
    public boolean isBronzeAge() { return this.bronzeAge; }
    public boolean isInternetAge() { return this.internetAge; }
}
