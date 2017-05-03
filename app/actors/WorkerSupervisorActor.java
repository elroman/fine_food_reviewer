package actors;

import actors.cmd.StartParseCmd;
import actors.proto.GetTopListByCountersReq;
import actors.proto.GetTopListByCountersRes;
import actors.proto.GetTopListsReq;
import actors.proto.GetTopListsRes;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.dispatch.Mapper;
import akka.dispatch.Recover;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.event.LoggingReceive;
import akka.japi.pf.ReceiveBuilder;
import models.AbstractCounterMessages;
import models.Review;
import play.Logger;
import scala.PartialFunction;
import scala.Tuple2;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;
import scala.runtime.BoxedUnit;
import service.ParseFileService;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

public class WorkerSupervisorActor extends AbstractActor {
    private final String PATH_TO_FILE = getContext().system().settings().config().getString("file_system.path_to_reviews_file");
    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    @Inject
    ParseFileService parseFileService;

    @Inject
    @Named("userHandlerActor")
    ActorRef userHandlerActor;

    @Inject
    @Named("foodHandlerActor")
    ActorRef foodHandlerActor;

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return LoggingReceive.create(ReceiveBuilder
                .match(StartParseCmd.class, this::startParse)
                .match(GetTopListsReq.class, this::getTopLists)
                .build(), getContext());
    }

    private void startParse(StartParseCmd cmd) {
        ResultSet resultSet = parseFileService.getResultSetFromCsv(PATH_TO_FILE);
        logger.debug("Parse file started");
        try {
            resultSet.next(); // first row is a head, we will skip it
            long startTime = System.nanoTime();
            while (resultSet.next()) {
                Review review = getReviewFromResultSet(resultSet);
                userHandlerActor.forward(review, getContext());
                foodHandlerActor.forward(review, getContext());
            }
            logger.debug("Parse file finished. spent: {}", (System.nanoTime() - startTime) / 1000);
        } catch (SQLException e) {
            logger.error("startParse() ERROR: {}", e);
        }
    }

    private void getTopLists(GetTopListsReq req) {
        final ExecutionContextExecutor exec = getContext().system().dispatcher();
        final int top = req.getTop();

        Future<List<AbstractCounterMessages>> userTopListFuture = getUserTopList(top, exec);
        Future<List<AbstractCounterMessages>> foodTopListFuture = getFoodTopList(top, exec);

        Future<GetTopListsRes> resp = userTopListFuture.zip(foodTopListFuture)
                .map(new Mapper<Tuple2<List<AbstractCounterMessages>, List<AbstractCounterMessages>>, GetTopListsRes>() {
                    public GetTopListsRes apply(Tuple2<List<AbstractCounterMessages>, List<AbstractCounterMessages>> zipper) {

                        HashMap<String, List<AbstractCounterMessages>> topListMap = new HashMap<>();
                        topListMap.put("userTopList", zipper._1());
                        topListMap.put("foodTopList", zipper._2());

                        return new GetTopListsRes(topListMap);
                    }
                }, exec)
                .recover(new Recover<GetTopListsRes>() {
                    public GetTopListsRes recover(Throwable problem) throws Throwable {
                        Logger.error("WorkerSupervisorActor: getTopLists() error: {}", problem.getMessage());
                        return null;
                    }
                }, exec);

        pipe(resp, getContext().dispatcher()).to(sender());
    }

    private Future<List<AbstractCounterMessages>> getUserTopList(int top, ExecutionContextExecutor exec) {
        return ask(userHandlerActor, new GetTopListByCountersReq(top), 5000)
                .map(new Mapper<Object, List<AbstractCounterMessages>>() {
                    @Override
                    public List<AbstractCounterMessages> apply(Object parameter) {
                        return ((GetTopListByCountersRes) parameter).getTopList();
                    }
                }, exec)
                .recover(new Recover<List<AbstractCounterMessages>>() {
                    public List<AbstractCounterMessages> recover(Throwable problem) throws Throwable {
                        Logger.error("WorkerSupervisorActor: getUserTopList() error: {}", problem.getMessage());
                        return new ArrayList<>();
                    }
                }, exec);
    }

    private Future<List<AbstractCounterMessages>> getFoodTopList(int top, ExecutionContextExecutor exec) {
        return ask(foodHandlerActor, new GetTopListByCountersReq(top), 5000)
                .map(new Mapper<Object, List<AbstractCounterMessages>>() {
                    @Override
                    public List<AbstractCounterMessages> apply(Object parameter) {
                        return ((GetTopListByCountersRes) parameter).getTopList();
                    }
                }, exec)
                .recover(new Recover<List<AbstractCounterMessages>>() {
                    public List<AbstractCounterMessages> recover(Throwable problem) throws Throwable {
                        Logger.error("WorkerSupervisorActor: getFoodTopList() error: {}", problem.getMessage());
                        return new ArrayList<>();
                    }
                }, exec);
    }

    private Review getReviewFromResultSet(ResultSet resultSet) throws SQLException {
        return new Review(
                resultSet.getString("id"),
                resultSet.getString("productId"),
                resultSet.getString("userId"),
                resultSet.getString("profileName"),
                resultSet.getString("helpfulnessNumerator"),
                resultSet.getString("helpfulnessDenominator"),
                resultSet.getString("score"),
                resultSet.getString("time"),
                resultSet.getString("summary"),
                resultSet.getString("text"));
    }
}