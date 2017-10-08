package models.review;

public class Product
    implements Countable {

    private String productId;

    public Product(final String productId) {
        this.productId = productId;
    }

    @Override
    public String getId() {
        return productId;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = (31 * hash) + productId.hashCode();
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

        if (obj instanceof Product) {
            final Product other = (Product) obj;
            if (other.productId.equals(productId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "Product{" +
            "productId='" + productId + '\'' +
            '}';
    }
}
