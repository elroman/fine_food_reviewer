package actors;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.LoggingReceive;
import akka.japi.pf.ReceiveBuilder;
import models.review.Comment;
import models.review.CommentWord;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class TextParserActor
    extends AbstractActor {

    @Inject
    @Named("wordHandlerActor")
    ActorRef wordHandlerActor;

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return LoggingReceive.create(ReceiveBuilder
                                         .match(Comment.class, this::handleComment)
                                         .build(), getContext());
    }

    private void handleComment(Comment comment) {
        Arrays.stream(normalizeText(comment.getComment()).split("&"))
            .filter(elem -> !elem.equals(""))
            .forEach(
                elem -> wordHandlerActor.forward(new CommentWord(elem), getContext())
            );
    }

    private String normalizeText(String text) {
        return
            text
                .replaceAll("\\W", "&")
                .toLowerCase();
    }
}