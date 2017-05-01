package controllers;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;

public class FileController extends Controller {

    private final String PATH_TO_FILE = Akka.system().settings().config().getString("file_system.path_to_reviews_file");
    private LoggingAdapter logger = Logging.getLogger(Akka.system(), this);

    public F.Promise<Result> startParseFile() {

        return F.Promise.pure(ok("startedParse"));
    }


}
