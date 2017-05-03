package controllers;

import actors.cmd.StartParseCmd;
import akka.actor.ActorRef;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import javax.inject.Inject;
import javax.inject.Named;

public class FileController extends Controller {

    @Inject
    @Named("workerSupervisorActor")
    ActorRef workerSupervisorActor;

    public F.Promise<Result> startParseFile() {
        workerSupervisorActor.tell(new StartParseCmd(), ActorRef.noSender());
        return F.Promise.pure((Result) redirect(routes.ReviewerController.index()));
    }
}
