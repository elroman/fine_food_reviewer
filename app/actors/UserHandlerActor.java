package actors;

import models.CounterUserActivity;
import models.Review;

public class UserHandlerActor extends AbstractCounterActor {

    @Override
    void handleNewReview(Review review) {
        final String userId = review.getUserId();
        counterReviewsMap.put(userId, new CounterUserActivity(userId, review.getProfileName(), getNewCounter(userId)));
    }
}