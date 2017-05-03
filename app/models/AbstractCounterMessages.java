package models;

public abstract class AbstractCounterMessages {
    private String id;
    private int counter;

    public AbstractCounterMessages(String id, int counter) {
        this.id = id;
        this.counter = counter;
    }

    public String getId() {
        return id;
    }

    public int getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return "AbstractCounterMessages{" +
                "id='" + id + '\'' +
                ", counter=" + counter +
                '}';
    }
}
