package actors.proto;

import java.util.LinkedHashMap;
import java.util.Map;

import models.review.Countable;

public class GetTopListsRes {

    private Map<String, LinkedHashMap<Countable, Integer>> topListsMap;

    public GetTopListsRes(Map<String, LinkedHashMap<Countable, Integer>> topListsMap) {
        this.topListsMap = topListsMap;
    }

    public Map<String, LinkedHashMap<Countable, Integer>> getTopListsMap() {
        return topListsMap;
    }
}
