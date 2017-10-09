package actors;

import static akka.pattern.Patterns.pipe;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import actors.cmd.ClearMapCmd;
import actors.proto.GetTopListByCountersReq;
import actors.proto.GetTopListByCountersRes;
import akka.actor.AbstractActor;
import akka.dispatch.Futures;
import akka.event.LoggingReceive;
import akka.japi.pf.ReceiveBuilder;
import models.review.Countable;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public abstract class AbstractCounterActor
    extends AbstractActor {

    final Map<Countable, Integer> counterReviewsMap = new HashMap<>();

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return LoggingReceive.create(ReceiveBuilder
                                         .match(Countable.class, this::handleNewReview)
                                         .match(ClearMapCmd.class, this::clearMap)
                                         .match(GetTopListByCountersReq.class, this::getTopList)
                                         .build(), getContext());
    }

    private void handleNewReview(Countable countableObject) {
        counterReviewsMap.put(countableObject, iterateCounter(countableObject));
    }

    private void getTopList(GetTopListByCountersReq req) {
        final LinkedHashMap<Countable, Integer> topList = counterReviewsMap.entrySet().parallelStream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(req.getTop())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                      (oldValue, newValue) -> oldValue, LinkedHashMap::new
            ));
        pipe(Futures.successful(new GetTopListByCountersRes(topList)), getContext().dispatcher()).to(sender());
    }

    Integer iterateCounter(Countable countableObject) {
        final Integer counter = counterReviewsMap.get(countableObject);
        return (counter != null) ? (1 + counter) : 1;
    }

    private void clearMap(ClearMapCmd cmd) {
        counterReviewsMap.clear();
    }
}