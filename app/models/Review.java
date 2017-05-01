package models;

public class Review {

    private String id;
    private String ProductId;
    private String UserId;
    private String ProfileName;
    private String HelpfulnessNumerator;
    private String HelpfulnessDenominator;
    private String Score;
    private String Time;
    private String Summary;
    private String Text;

    public Review(String id, String productId, String userId, String profileName, String helpfulnessNumerator, String helpfulnessDenominator, String score, String time, String summary, String text) {
        this.id = id;
        ProductId = productId;
        UserId = userId;
        ProfileName = profileName;
        HelpfulnessNumerator = helpfulnessNumerator;
        HelpfulnessDenominator = helpfulnessDenominator;
        Score = score;
        Time = time;
        Summary = summary;
        Text = text;
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return ProductId;
    }

    public String getUserId() {
        return UserId;
    }

    public String getProfileName() {
        return ProfileName;
    }

    public String getHelpfulnessNumerator() {
        return HelpfulnessNumerator;
    }

    public String getHelpfulnessDenominator() {
        return HelpfulnessDenominator;
    }

    public String getScore() {
        return Score;
    }

    public String getTime() {
        return Time;
    }

    public String getSummary() {
        return Summary;
    }

    public String getText() {
        return Text;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", ProductId='" + ProductId + '\'' +
                ", UserId='" + UserId + '\'' +
                ", ProfileName='" + ProfileName + '\'' +
                ", HelpfulnessNumerator='" + HelpfulnessNumerator + '\'' +
                ", HelpfulnessDenominator='" + HelpfulnessDenominator + '\'' +
                ", Score='" + Score + '\'' +
                ", Time='" + Time + '\'' +
                ", Summary='" + Summary + '\'' +
                ", Text='" + Text + '\'' +
                '}';
    }
}
