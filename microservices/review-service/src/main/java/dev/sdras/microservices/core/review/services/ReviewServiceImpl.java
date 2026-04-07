package dev.sdras.microservices.core.review.services;

import dev.sdras.api.core.review.Review;
import dev.sdras.api.core.review.ReviewService;
import dev.sdras.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;

    public ReviewServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Review> getReviews(int productId) {
        LOG.debug("/review returning reviews for productId={}", productId);

        List<Review> mockReviews = List.of(
            new Review(1, 1, "Alice",  "Great product!", "Really enjoyed using it.", serviceUtil.getServerAddress()),
            new Review(2, 2, "Bob",    "Not bad",        "Does the job, but could be better.", serviceUtil.getServerAddress()),
            new Review(3, 3, "Carlos", "Highly recommended", "Best purchase I've made this year.", serviceUtil.getServerAddress())
        );

        return mockReviews.stream()
                .filter(review -> review.getProductId() == productId)
                .toList();
    }
}
