package modules;

import actors.FoodHandlerActor;
import actors.TextParserActor;
import actors.UserHandlerActor;
import actors.WordHandlerActor;
import actors.WorkerSupervisorActor;
import com.google.inject.AbstractModule;
import play.libs.akka.AkkaGuiceSupport;

public class MyModule extends AbstractModule implements AkkaGuiceSupport {
    @Override
    protected void configure() {
        bindActor(WorkerSupervisorActor.class, "workerSupervisorActor");
        bindActor(UserHandlerActor.class, "userHandlerActor");
        bindActor(FoodHandlerActor.class, "foodHandlerActor");
        bindActor(WordHandlerActor.class, "wordHandlerActor");
        bindActor(TextParserActor.class, "textParserActor");
    }
}