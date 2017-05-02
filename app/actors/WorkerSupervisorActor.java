package actors;

import actors.cmd.StartParseCmd;
import actors.proto.*;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
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
import service.ParseFileService;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return LoggingReceive.create(ReceiveBuilder
                .match(StartParseCmd.class, this::startParse)
                .match(GetTopListsReq.class, this::getTopLists)
                .build(), getContext());
    }

    private void startParse(StartParseCmd cmd) {
        ResultSet resultSet = parseFileService.getResultSetFromCsv(PATH_TO_FILE);

        logger.debug("=== Parse file started ===");
        try {
            resultSet.next(); // first row is a head, we will skip it

            int counter = 0;
            long startTime = System.nanoTime();
            while (resultSet.next()) {

                Review review = getReviewFromResultSet(resultSet);

                userHandlerActor.forward(review, getContext());
                counter++;
                if (counter % 1000 == 0) {
                    logger.debug(" ___ parsed: {}", counter);
                }
            }

            logger.debug("=== Parse file finished ===  spent: {}",(System.nanoTime() - startTime)/1000);

        } catch (SQLException e) {
            logger.error("startParse() ERROR: {}", e);
        }
    }

    private void getTopLists(GetTopListsReq req) {
        final ExecutionContextExecutor exec = getContext().system().dispatcher();
        final int top = req.getTop();

        Future<List<AmountUserMessages>> userTopList = getTopUserLists(top, exec);

        Future<GetTopListsRes> resp = userTopList
                .map(new Mapper<List<AmountUserMessages>, GetTopListsRes>() {
                    @Override
                    public GetTopListsRes apply(List<AmountUserMessages> userTopList) {
                        return new GetTopListsRes(userTopList);
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

    private Future<List<AmountUserMessages>> getTopUserLists(int top, ExecutionContextExecutor exec) {
        return ask(userHandlerActor, new GetTopListUsersReq(top), 5000)
                .map(new Mapper<Object, List<AmountUserMessages>>() {
                    @Override
                    public List<AmountUserMessages> apply(Object parameter) {
                        return ((GetTopListUsersRes) parameter).getUserList();
                    }
                }, exec)
                .recover(new Recover<List<AmountUserMessages>>() {
                    public List<AmountUserMessages> recover(Throwable problem) throws Throwable {
                        Logger.error("WorkerSupervisorActor: getTopUserLists() error: {}", problem.getMessage());
                        return new ArrayList<>();
                    }
                }, exec);
    }


    private Review getReviewFromResultSet(ResultSet resultSet) throws SQLException {
        String userId = resultSet.getString("userId");
        if (userId.contains("#")) {
            userId = userId.replace("#", "_");
            Logger.error("WorkerSupervisorActor: getReviewFromResultSet()  invalid userId : {}   will be changed on '_'", userId);
        }

        return new Review(
                resultSet.getString("id"),
                resultSet.getString("productId"),
                userId,
                resultSet.getString("profileName"),
                resultSet.getString("helpfulnessNumerator"),
                resultSet.getString("helpfulnessDenominator"),
                resultSet.getString("score"),
                resultSet.getString("time"),
                resultSet.getString("summary"),
                resultSet.getString("text"));
    }
}