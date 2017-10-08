package models.review;

public class Reviewer
    implements Countable {

    private String userId;
    private String profileName;

    public Reviewer(final String userId, final String profileName) {
        this.userId = userId;
        this.profileName = profileName;
    }

    @Override
    public String getId() {
        return userId;
    }

    public String getProfileName() {
        return profileName;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = (31 * hash) + userId.hashCode();
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        if (obj instanceof Reviewer) {
            final Reviewer other = (Reviewer) obj;
            if (other.userId.equals(userId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "Reviewer{" +
            "userId='" + userId + '\'' +
            ", profileName='" + profileName + '\'' +
            '}';
    }
}
