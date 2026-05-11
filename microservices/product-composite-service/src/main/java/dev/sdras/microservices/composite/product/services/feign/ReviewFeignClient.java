package dev.sdras.microservices.composite.product.services.feign;

import dev.sdras.api.core.review.Review;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("review-service")
public interface ReviewFeignClient {
    @PostMapping(
            value = "/review",
            consumes = "application/json",
            produces = "application/json")
    Review createReview(@RequestBody Review body);

    @GetMapping(
            value = "/review",
            produces = "application/json")
    List<Review> getReviews(@RequestParam(value = "productId") int productId);

    @DeleteMapping(value = "/review")
    void deleteReviews(@RequestParam(value = "productId") int productId);
}
