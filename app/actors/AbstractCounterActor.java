package actors;

import actors.proto.GetTopListByCountersReq;
import actors.proto.GetTopListByCountersRes;
import akka.actor.AbstractActor;
import akka.dispatch.Futures;
import akka.event.LoggingReceive;
import akka.japi.pf.ReceiveBuilder;
import models.AbstractCounterMessages;
import models.CounterCommentedFood;
import models.Review;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static akka.pattern.Patterns.pipe;

public abstract class AbstractCounterActor extends AbstractActor {

    Map<String, AbstractCounterMessages> counterReviewsMap = new HashMap<>();

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return LoggingReceive.create(ReceiveBuilder
                .match(Review.class, this::handleNewReview)
                .match(GetTopListByCountersReq.class, this::getTopList)
                .build(), getContext());
    }

    abstract void handleNewReview(Review review);

    private void getTopList(GetTopListByCountersReq req) {
        List<AbstractCounterMessages> topList =  counterReviewsMap.values().parallelStream()
                .sorted((f1, f2) -> Integer.compare(f2.getCounter(), f1.getCounter()))
                .limit(req.getTop())
                .collect(Collectors.toList());
        pipe(Futures.successful(new GetTopListByCountersRes(topList)), getContext().dispatcher()).to(sender());
    }

    int getNewCounter(String userId) {
        AbstractCounterMessages currentCounter = counterReviewsMap.get(userId);
        return currentCounter != null ? 1 + currentCounter.getCounter() : 1;
    }
}