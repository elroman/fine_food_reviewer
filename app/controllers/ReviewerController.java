package controllers;

import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.start_page;

public class ReviewerController extends Controller {

    public F.Promise<Result> index() {
        return F.Promise.pure(ok(start_page.render("Your new application is ready.")));
    }

}
