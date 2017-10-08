package actors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.LoggingReceive;
import akka.japi.pf.ReceiveBuilder;
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
                                         .match(String.class, this::handleComment)
                                         .build(), getContext());
    }

    private void handleComment(String comment) {

        final List<CommentWord> wordList = Arrays
            .stream(
                comment
                    .replace(".", " ")
                    .replace(",", " ")
                    .replace("(", " ")
                    .replace(")", " ")
                    .split(" ")
            )
            .map(CommentWord::new)
            .collect(Collectors.toList());

        wordList.forEach(elem -> wordHandlerActor.forward(elem, getContext()));
    }
}