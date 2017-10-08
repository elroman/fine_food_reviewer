package models.review;

public class CommentWord
    implements Countable {

    private String word;

    public CommentWord(final String word) {
        this.word = word.toLowerCase();
    }

    @Override
    public String getId() {
        return word;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = (31 * hash) + word.hashCode();
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

        if (obj instanceof CommentWord) {
            final CommentWord other = (CommentWord) obj;
            if (other.word.equals(word)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "CommentWord{" +
            "word='" + word + '\'' +
            '}';
    }
}
