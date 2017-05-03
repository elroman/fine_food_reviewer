package actors.proto;

import models.AbstractCounterMessages;

import java.util.List;

public class GetTopListByCountersRes {

    private List<AbstractCounterMessages> topList;

    public GetTopListByCountersRes(List<AbstractCounterMessages> topList) {
        this.topList = topList;
    }

    public List<AbstractCounterMessages> getTopList() {
        return topList;
    }

    @Override
    public String toString() {
        return "GetTopListByCountersRes{" +
                "topList=" + topList +
                '}';
    }
}
