package models.review;

public class Comment {

    private String comment;

    public Comment(final String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "Comment{" +
            "comment='" + comment + '\'' +
            '}';
    }
}
