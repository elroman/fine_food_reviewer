package actors;

import models.CounterCommentedFood;
import models.Review;

public class FoodHandlerActor extends AbstractCounterActor {

    @Override
    void handleNewReview(Review review) {
        final String productId = review.getProductId();
        super.counterReviewsMap.put(productId, new CounterCommentedFood(productId, super.getNewCounter(productId)));
    }
}