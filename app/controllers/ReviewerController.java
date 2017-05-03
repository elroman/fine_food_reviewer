package controllers;

import actors.proto.GetTopListsReq;
import actors.proto.GetTopListsRes;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import models.AbstractCounterMessages;
import models.CounterCommentedFood;
import models.CounterUserActivity;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.start_page;
import views.html.top_list_page;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static akka.pattern.Patterns.ask;
import static java.util.Comparator.comparing;


public class ReviewerController extends Controller {
    final private LoggingAdapter logger = Logging.getLogger(Akka.system(), this);

    @Inject
    @Named("workerSupervisorActor")
    ActorRef workerSupervisorActor;

    public F.Promise<Result> index() {
        return F.Promise.pure(ok(start_page.render("Your new application is ready.")));
    }

    public F.Promise<Result> getTopLists() {
        return F.Promise.wrap(ask(workerSupervisorActor, new GetTopListsReq(1000), 10000))
                .map(res -> {
                    Map<String, List<AbstractCounterMessages>> topListMap = ((GetTopListsRes) res).getTopListsMap();

                    List<CounterUserActivity> userTopList = topListMap.get("userTopList").stream()
                            .sorted(comparing((AbstractCounterMessages elem) -> ((CounterUserActivity)elem).getProfileName()))
                            .map(elem->(CounterUserActivity)elem)
                            .collect(Collectors.toList());

                    List<CounterCommentedFood> foodTopList = topListMap.get("foodTopList").stream()
                            .sorted(comparing(AbstractCounterMessages::getId))
                            .map(elem->(CounterCommentedFood) elem)
                            .collect(Collectors.toList());

                    return (Result) ok(top_list_page.render("Your new application is ready", userTopList, foodTopList));
                })
                .recover(t -> {
                    logger.error("Error: updateStatusNotificationPage() problem :" + t);
                    return ok("Error getTopLists()");
                });
    }
}
