package controllers;

import actors.proto.GetTopListsReq;
import actors.proto.GetTopListsRes;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import models.AmountUserMessages;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.start_page;
import views.html.top_list_page;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                    List<AmountUserMessages> userTopList = ((GetTopListsRes) res).getUserList();

                    return (Result) ok(top_list_page.render("Your new application is ready", userTopList.stream().sorted(comparing(AmountUserMessages::getProfileName)).collect(Collectors.toList())));
                })
                .recover(t -> {
                    logger.error("Error: updateStatusNotificationPage() problem :" + t);
                    return ok("Error getTopLists()");
                });
    }
}
