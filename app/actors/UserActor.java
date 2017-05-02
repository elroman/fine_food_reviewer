package actors;

import actors.proto.GetAmountUserMessagesReq;
import actors.proto.GetAmountUserMessagesRes;
import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.event.LoggingReceive;
import akka.japi.pf.ReceiveBuilder;
import models.AmountUserMessages;
import models.Review;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import static akka.pattern.Patterns.pipe;

public class UserActor extends AbstractActor {
    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private String userId;
    private String profileName;
    private int counterMessages;

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return LoggingReceive.create(ReceiveBuilder
                .match(Review.class, this::handleField)
                .match(GetAmountUserMessagesReq.class, this::getCountMessages)
                .build(), getContext());
    }

    public static Props props(String userId, String profileName) {
        return Props.create(UserActor.class, () -> new UserActor(userId, profileName));
    }

    public UserActor(String userId, String profileName) {
        this.userId = userId;
        this.profileName = profileName;
    }

    private void getCountMessages(GetAmountUserMessagesReq req) {
        pipe(Futures.successful(new GetAmountUserMessagesRes(new AmountUserMessages(userId, profileName, counterMessages))), getContext().dispatcher()).to(sender());
    }


    private void handleField(Review review) {
        counterMessages++;
        /*logger.debug("User {} counterMessages: {}", profileName, counterMessages);*/
    }

}
