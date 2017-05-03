package actors.proto;

public class GetTopListByCountersReq {
    private int top;

    public GetTopListByCountersReq(int top) {
        this.top = top;
    }

    public int getTop() {
        return top;
    }
}
