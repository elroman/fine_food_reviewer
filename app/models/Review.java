package models;

public class Review {

    private String id;
    private String productId;
    private String userId;
    private String profileName;
    private String helpfulnessNumerator;
    private String helpfulnessDenominator;
    private String score;
    private String time;
    private String summary;
    private String text;

    public Review(String id, String productId, String userId, String profileName, String helpfulnessNumerator, String helpfulnessDenominator, String score, String time, String summary, String text) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.profileName = profileName;
        this.helpfulnessNumerator = helpfulnessNumerator;
        this.helpfulnessDenominator = helpfulnessDenominator;
        this.score = score;
        this.time = time;
        this.summary = summary;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getHelpfulnessNumerator() {
        return helpfulnessNumerator;
    }

    public String getHelpfulnessDenominator() {
        return helpfulnessDenominator;
    }

    public String getScore() {
        return score;
    }

    public String getTime() {
        return time;
    }

    public String getSummary() {
        return summary;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", userId='" + userId + '\'' +
                ", profileName='" + profileName + '\'' +
                ", helpfulnessNumerator='" + helpfulnessNumerator + '\'' +
                ", helpfulnessDenominator='" + helpfulnessDenominator + '\'' +
                ", score='" + score + '\'' +
                ", time='" + time + '\'' +
                ", summary='" + summary + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
