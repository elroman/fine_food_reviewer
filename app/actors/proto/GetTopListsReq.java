package actors.proto;

public class GetTopListsReq {
    private int top;

    public GetTopListsReq(int top) {
        this.top = top;
    }

    public int getTop() {
        return top;
    }
}
