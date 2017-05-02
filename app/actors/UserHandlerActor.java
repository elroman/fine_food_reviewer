package actors;

import actors.proto.GetAmountUserMessagesReq;
import actors.proto.GetAmountUserMessagesRes;
import actors.proto.GetTopListUsersReq;
import actors.proto.GetTopListUsersRes;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.InvalidActorNameException;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.dispatch.Recover;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.event.LoggingReceive;
import akka.japi.pf.ReceiveBuilder;
import models.AmountUserMessages;
import models.Review;
import play.Logger;
import scala.PartialFunction;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;
import scala.runtime.BoxedUnit;

import java.util.*;


import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;
import static java.util.Comparator.comparing;

public class UserHandlerActor extends AbstractActor {

    Map<String, ActorRef> userActorMap = new HashMap<>();
    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return LoggingReceive.create(ReceiveBuilder
                .match(Review.class, this::handleField)
                .match(GetTopListUsersReq.class, this::getTopUsers)
                .build(), getContext());
    }

    private void handleField(Review review) {
        String userId = review.getUserId();
        String profileName = review.getProfileName();

        ActorRef userActorRef = userActorMap.get(userId);

        if (userActorRef == null) {
//            logger.debug("UserHandler: create actor for user  {} ", profileName );
            try {
                userActorRef = getContext().actorOf(UserActor.props(userId, profileName), userId);
                userActorMap.put(userId, userActorRef);
            }catch (InvalidActorNameException ex){
                Logger.error("UserHandlerActor: handleField() InvalidActorNameException  userId: {}", userId);
            }

        }

        userActorRef.forward(review, getContext());
    }


    private void getTopUsers(GetTopListUsersReq req) {
        final ExecutionContextExecutor exec = getContext().system().dispatcher();
        final int top = req.getTop();

        List<Future<AmountUserMessages>> messageList = getAmountMessageListFuture(exec);

        Future<GetTopListUsersRes> resp = getSortedAmountMapFuture(messageList, exec)
                .map(new Mapper<Map<Integer, List<AmountUserMessages>>, GetTopListUsersRes>() {
                    @Override
                    public GetTopListUsersRes apply(Map<Integer, List<AmountUserMessages>> sortedMap) {

                        List<AmountUserMessages> userTopList = new ArrayList<>();

                        for(Map.Entry<Integer, List<AmountUserMessages>> elem : sortedMap.entrySet()){

                            Integer count = elem.getKey();
                            List<AmountUserMessages> users = elem.getValue();

                            for (int i = 0; i < users.size(); i++) {
                                userTopList.add(users.get(i));
                                if(userTopList.size()>=top){
                                    break;
                                }
                            }

                            if(userTopList.size()>=top){
                                break;
                            }
                        }
                        return new GetTopListUsersRes(userTopList);
                    }
                }, exec)
                .recover(new Recover<GetTopListUsersRes>() {
                    public GetTopListUsersRes recover(Throwable problem) throws Throwable {
                        Logger.error("UserHandlerActor: getTopUsers() error: {}", problem.getMessage());
                        return new GetTopListUsersRes(new ArrayList<>());
                    }
                }, exec);

        pipe(resp, getContext().dispatcher()).to(sender());
    }

    private List<Future<AmountUserMessages>> getAmountMessageListFuture(ExecutionContextExecutor exec) {
        List<Future<AmountUserMessages>> messageList = new ArrayList<>();

        userActorMap.forEach((userId, userActorRef) -> {
            Future<AmountUserMessages> resp = ask(userActorRef, new GetAmountUserMessagesReq(), 1000)
                    .map(new Mapper<Object, AmountUserMessages>() {
                        @Override
                        public AmountUserMessages apply(Object parameter) {
                            return ((GetAmountUserMessagesRes) parameter).getAmountUserMessages();
                        }
                    }, exec)
                    .recover(new Recover<AmountUserMessages>() {
                        public AmountUserMessages recover(Throwable problem) throws Throwable {
                            Logger.error("UserHandlerActor: getAmountMessageListFuture() error: {}", problem.getMessage());
                            return null;
                        }
                    }, exec);

            messageList.add(resp);
        });
        return messageList;
    }

    private Future<Map<Integer, List<AmountUserMessages>>> getSortedAmountMapFuture(List<Future<AmountUserMessages>> messageList, ExecutionContextExecutor exec) {
        return Futures.sequence(messageList, context().dispatcher())
                .map(new Mapper<Iterable<AmountUserMessages>, Map<Integer, List<AmountUserMessages>>>() {
                    @Override
                    public Map<Integer, List<AmountUserMessages>> apply(Iterable<AmountUserMessages> amountMessages) {

                        Map<Integer, List<AmountUserMessages>> sortedMap = new TreeMap<>(Collections.reverseOrder());

                        amountMessages.forEach(elem -> {
                            if (elem != null) {
                                int countMessages = elem.getCounterMessages();

                                List<AmountUserMessages> amountUserList = sortedMap.get(countMessages);

                                if (amountUserList == null) {
                                    amountUserList = new ArrayList<>();
                                }
                                amountUserList.add(elem);
                                sortedMap.put(countMessages, amountUserList);
                            }
                        });

                        return sortedMap;
                    }
                }, exec)
                .recover(new Recover<Map<Integer, List<AmountUserMessages>>>() {
                    public Map<Integer, List<AmountUserMessages>> recover(Throwable problem) throws Throwable {
                        Logger.error("UserHandlerActor: getSortedAmountMapFuture() error: {}", problem.getMessage());
                        return null;
                    }
                }, exec);
    }


}
