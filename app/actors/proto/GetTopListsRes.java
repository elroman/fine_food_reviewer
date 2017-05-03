package actors.proto;

import models.AbstractCounterMessages;

import java.util.List;
import java.util.Map;

public class GetTopListsRes {

    private Map<String, List<AbstractCounterMessages>> topListsMap;

    public GetTopListsRes(Map<String, List<AbstractCounterMessages>> topListsMap) {
        this.topListsMap = topListsMap;
    }

    public Map<String, List<AbstractCounterMessages>> getTopListsMap() {
        return topListsMap;
    }
}
