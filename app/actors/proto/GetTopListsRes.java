package actors.proto;

import models.AmountUserMessages;

import java.util.List;

public class GetTopListsRes {

    private List<AmountUserMessages> userList;

    public GetTopListsRes(List<AmountUserMessages> userList) {
        this.userList = userList;
    }

    public List<AmountUserMessages> getUserList() {
        return userList;
    }

    @Override
    public String toString() {
        return "GetTopListsRes{" +
                "userList=" + userList +
                '}';
    }
}
