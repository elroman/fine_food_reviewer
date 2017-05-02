package actors.proto;

public class GetTopListUsersReq {
    private int top;

    public GetTopListUsersReq(int top) {
        this.top = top;
    }

    public int getTop() {
        return top;
    }
}
