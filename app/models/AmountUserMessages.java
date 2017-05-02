package models;

public class AmountUserMessages {
    private String userId;
    private String profileName;
    private int counterMessages;

    public AmountUserMessages(String userId, String profileName, int counterMessages) {
        this.userId = userId;
        this.profileName = profileName;
        this.counterMessages = counterMessages;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfileName() {
        return profileName;
    }

    public int getCounterMessages() {
        return counterMessages;
    }

    @Override
    public String toString() {
        return "AmountUserMessages{" +
                "userId='" + userId + '\'' +
                ", profileName='" + profileName + '\'' +
                ", counterMessages=" + counterMessages +
                '}';
    }
}
