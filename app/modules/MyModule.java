package modules;

import actors.FoodHandlerActor;
import actors.UserHandlerActor;
import actors.WorkerSupervisorActor;
import com.google.inject.AbstractModule;
import play.libs.akka.AkkaGuiceSupport;

public class MyModule extends AbstractModule implements AkkaGuiceSupport {
    @Override
    protected void configure() {
        bindActor(WorkerSupervisorActor.class, "workerSupervisorActor");
        bindActor(UserHandlerActor.class, "userHandlerActor");
        bindActor(FoodHandlerActor.class, "foodHandlerActor");
    }
}