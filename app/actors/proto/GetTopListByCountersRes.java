package actors.proto;

import java.util.LinkedHashMap;

import models.review.Countable;

public class GetTopListByCountersRes {

    private LinkedHashMap<Countable, Integer> topList;

    public GetTopListByCountersRes(LinkedHashMap<Countable, Integer> topList) {
        this.topList = topList;
    }

    public LinkedHashMap<Countable, Integer> getTopList() {
        return topList;
    }

    @Override
    public String toString() {
        return "GetTopListByCountersRes{" +
            "topList=" + topList +
            '}';
    }
}
