package controllers;

import static akka.pattern.Patterns.ask;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import actors.proto.GetTopListsReq;
import actors.proto.GetTopListsRes;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import models.review.Countable;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.start_page;
import views.html.top_list_page;

public class ReviewerController
    extends Controller {

    final private LoggingAdapter logger = Logging.getLogger(Akka.system(), this);

    @Inject
    @Named("workerSupervisorActor")
    ActorRef workerSupervisorActor;

    public F.Promise<Result> index() {
        return F.Promise.pure(ok(start_page.render("Your new application is ready.")));
    }

    public F.Promise<Result> getTopLists() {
        return F.Promise.wrap(ask(workerSupervisorActor, new GetTopListsReq(100), 100000))
            .map(res -> {
                Map<String, LinkedHashMap<Countable, Integer>> topListMap = ((GetTopListsRes) res).getTopListsMap();

                return (Result) ok(top_list_page.render(
                    "Your new application is ready",
                    topListMap.get("userTopList"),
                    topListMap.get("foodTopList"),
                    topListMap.get("wordTopList")
                ));
            })
            .recover(t -> {
                logger.error("Error: updateStatusNotificationPage() problem :" + t);
                return ok("Error getTopLists()");
            });
    }
}
