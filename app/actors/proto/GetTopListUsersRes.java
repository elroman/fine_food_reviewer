package actors.proto;

import models.AmountUserMessages;

import java.util.List;

public class GetTopListUsersRes {

    private List<AmountUserMessages> userList;

    public GetTopListUsersRes(List<AmountUserMessages> userList) {
        this.userList = userList;
    }

    public List<AmountUserMessages> getUserList() {
        return userList;
    }

    @Override
    public String toString() {
        return "GetTopListUsersRes{" +
                "userList=" + userList +
                '}';
    }
}
