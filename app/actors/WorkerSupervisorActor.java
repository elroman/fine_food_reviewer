package actors;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.inject.Inject;
import javax.inject.Named;

import actors.cmd.ClearMapCmd;
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
import models.review.Comment;
import models.review.Countable;
import models.review.Product;
import models.review.Review;
import models.review.Reviewer;
import play.Logger;
import scala.PartialFunction;
import scala.Tuple2;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;
import scala.runtime.BoxedUnit;
import service.ParseFileService;

public class WorkerSupervisorActor
    extends AbstractActor {

    private final ExecutionContextExecutor exec = getContext().system().dispatcher();

    private final String PATH_TO_FILE = getContext().system()
        .settings()
        .config()
        .getString("file_system.path_to_reviews_file");
    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    @Inject
    ParseFileService parseFileService;

    @Inject
    @Named("userHandlerActor")
    ActorRef userHandlerActor;

    @Inject
    @Named("foodHandlerActor")
    ActorRef foodHandlerActor;

    @Inject
    @Named("wordHandlerActor")
    ActorRef wordHandlerActor;

    @Inject
    @Named("textParserActor")
    ActorRef textParserActor;

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

        ClearMapCmd clearMapCmd = new ClearMapCmd();
        userHandlerActor.forward(clearMapCmd, getContext());
        foodHandlerActor.forward(clearMapCmd, getContext());
        wordHandlerActor.forward(clearMapCmd, getContext());

        try {
            resultSet.next(); // first row is a head, we will skip it
            long startTime = System.nanoTime();

            int counter = 0;
            while (resultSet.next()) {
                final Review review = getReviewFromResultSet(resultSet);
                userHandlerActor.forward(review.getReviewer(), getContext());
                foodHandlerActor.forward(review.getProduct(), getContext());
                textParserActor.forward(review.getComment(), getContext());

                counter++;
                if ((counter % 10000) == 0) {
                    logger.debug(" == Parsed : {}", counter);
                }
            }
            logger.debug("Parse file finished. spent: {}", (System.nanoTime() - startTime) / 1000);
        } catch (SQLException e) {
            logger.error("startParse() ERROR: {}", e);
        }
    }

    private void getTopLists(GetTopListsReq req) {

        final int top = req.getTop();

        Future<LinkedHashMap<Countable, Integer>> userTopListFuture = getTopList(userHandlerActor, top);
        Future<LinkedHashMap<Countable, Integer>> foodTopListFuture = getTopList(foodHandlerActor, top);
        Future<LinkedHashMap<Countable, Integer>> wordTopListFuture = getTopList(wordHandlerActor, top);

        final Future<GetTopListsRes> resp = userTopListFuture.zip(foodTopListFuture.zip(wordTopListFuture))
            .map(new Mapper<
                Tuple2<
                    LinkedHashMap<Countable, Integer>,
                    Tuple2<LinkedHashMap<Countable, Integer>,
                        LinkedHashMap<Countable, Integer>>
                    >,
                GetTopListsRes>() {
                public GetTopListsRes apply(
                    Tuple2<
                        LinkedHashMap<Countable, Integer>,
                        Tuple2<LinkedHashMap<Countable, Integer>,
                            LinkedHashMap<Countable, Integer>>
                        > zipper
                ) {

                    HashMap<String, LinkedHashMap<Countable, Integer>> topListMap = new HashMap<>();
                    topListMap.put("userTopList", zipper._1());
                    topListMap.put("foodTopList", zipper._2()._1());
                    topListMap.put("wordTopList", zipper._2()._2());

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

    private Future<LinkedHashMap<Countable, Integer>> getTopList(ActorRef actorRef, int top) {
        return ask(actorRef, new GetTopListByCountersReq(top), 5000)
            .map(new Mapper<Object, LinkedHashMap<Countable, Integer>>() {
                @Override
                public LinkedHashMap<Countable, Integer> apply(Object parameter) {
                    return ((GetTopListByCountersRes) parameter).getTopList();
                }
            }, exec)
            .recover(new Recover<LinkedHashMap<Countable, Integer>>() {
                public LinkedHashMap<Countable, Integer> recover(Throwable problem) throws Throwable {
                    Logger.error("WorkerSupervisorActor: getTopList() error: {}", problem.getMessage());
                    return new LinkedHashMap<>();
                }
            }, exec);
    }

    private Review getReviewFromResultSet(ResultSet resultSet) throws SQLException {

        return new Review(
            resultSet.getString("id"),
            new Product(resultSet.getString("productId")),
            new Reviewer(
                resultSet.getString("userId"),
                resultSet.getString("profileName")
            ),
            resultSet.getString("helpfulnessNumerator"),
            resultSet.getString("helpfulnessDenominator"),
            resultSet.getString("score"),
            resultSet.getString("time"),
            resultSet.getString("summary"),
            new Comment(resultSet.getString("text"))
        );
    }
}