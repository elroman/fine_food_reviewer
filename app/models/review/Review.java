package models.review;

public class Review {

    private String id;
    private Product product;
    private Reviewer reviewer;
    private String helpfulnessNumerator;
    private String helpfulnessDenominator;
    private String score;
    private String time;
    private String summary;
    private String comment;

    public Review(
        String id,
        Product product,
        Reviewer reviewer,
        String helpfulnessNumerator,
        String helpfulnessDenominator,
        String score,
        String time,
        String summary,
        String comment
    ) {
        this.id = id;
        this.product = product;
        this.reviewer = reviewer;
        this.helpfulnessNumerator = helpfulnessNumerator;
        this.helpfulnessDenominator = helpfulnessDenominator;
        this.score = score;
        this.time = time;
        this.summary = summary;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Reviewer getReviewer() {
        return reviewer;
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

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "Review{" +
            "id='" + id + '\'' +
            ", product=" + product +
            ", reviewer=" + reviewer +
            ", helpfulnessNumerator='" + helpfulnessNumerator + '\'' +
            ", helpfulnessDenominator='" + helpfulnessDenominator + '\'' +
            ", score='" + score + '\'' +
            ", time='" + time + '\'' +
            ", summary='" + summary + '\'' +
            ", comment='" + comment + '\'' +
            '}';
    }
}
