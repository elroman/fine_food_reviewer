package models;

public class CounterUserActivity extends AbstractCounterMessages{
    private String profileName;

    public CounterUserActivity(String userId, String profileName, int messagesCounter) {
        super(userId, messagesCounter);
        this.profileName = profileName;
    }

    public String getProfileName() {
        return profileName;
    }

    @Override
    public String toString() {
        return "CounterUserActivity{" +
                "profileName='" + profileName + '\'' +
                '}';
    }
}
