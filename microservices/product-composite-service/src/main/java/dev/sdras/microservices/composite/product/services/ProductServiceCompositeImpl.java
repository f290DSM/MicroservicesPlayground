package dev.sdras.microservices.composite.product.services;

import dev.sdras.api.composite.product.ProductAggregate;
import dev.sdras.api.composite.product.ProductCompositeService;
import dev.sdras.api.composite.product.RecommendationSummary;
import dev.sdras.api.composite.product.ReviewSummary;
import dev.sdras.api.composite.product.ServiceAddresses;
import dev.sdras.api.core.product.Product;
import dev.sdras.api.core.recommendation.Recommendation;
import dev.sdras.api.core.review.Review;
import dev.sdras.api.exceptions.NotFoundException;
import dev.sdras.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductServiceCompositeImpl implements ProductCompositeService {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ProductServiceCompositeImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductCompositeIntegration productCompositeIntegration;

    public ProductServiceCompositeImpl(ServiceUtil serviceUtil, ProductCompositeIntegration productCompositeIntegration) {
        this.serviceUtil = serviceUtil;
        this.productCompositeIntegration = productCompositeIntegration;
    }

    @Override
    public void createProduct(ProductAggregate body) {
        LOG.info("Will create composite product for product.id={}", body.getProductId());

        Product product = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
        productCompositeIntegration.createProduct(product);

        if (body.getRecommendations() != null) {
            body.getRecommendations().forEach(recommendationSummary -> {
                Recommendation recommendation = new Recommendation(
                        body.getProductId(),
                        recommendationSummary.getRecommendationId(),
                        recommendationSummary.getAuthor(),
                        recommendationSummary.getRate(),
                        recommendationSummary.getContent(),
                        null
                );
                productCompositeIntegration.createRecommendation(recommendation);
            });
        }

        if (body.getReviews() != null) {
            body.getReviews().forEach(reviewSummary -> {
                Review review = new Review(
                        body.getProductId(),
                        reviewSummary.getReviewId(),
                        reviewSummary.getAuthor(),
                        reviewSummary.getSubject(),
                        reviewSummary.getContent(),
                        null
                );
                productCompositeIntegration.createReview(review);
            });
        }
    }

    @Override
    public ProductAggregate getProduct(int productId) {
        LOG.info("Will get composite product info for product.id={}", productId);

        Product product = productCompositeIntegration.getProduct(productId);
        if (product == null) {
            throw new NotFoundException("No product found for productId: " + productId);
        }

        List<Recommendation> recommendations = productCompositeIntegration.getRecommendations(productId);
        List<Review> reviews = productCompositeIntegration.getReviews(productId);

        List<RecommendationSummary> recommendationSummaries = recommendations == null ? null :
                recommendations.stream()
                        .map(recommendation -> new RecommendationSummary(
                                recommendation.getRecommendationId(),
                                recommendation.getAuthor(),
                                recommendation.getRate(),
                                recommendation.getContent()))
                        .toList();

        List<ReviewSummary> reviewSummaries = reviews == null ? null :
                reviews.stream()
                        .map(review -> new ReviewSummary(
                                review.getReviewId(),
                                review.getAuthor(),
                                review.getSubject(),
                                review.getContent()))
                        .toList();

        String reviewAddress = reviews != null && !reviews.isEmpty() ? reviews.get(0).getServiceAddress() : "";
        String recommendationAddress = recommendations != null && !recommendations.isEmpty()
                ? recommendations.get(0).getServiceAddress()
                : "";

        ServiceAddresses serviceAddresses = new ServiceAddresses(
                serviceUtil.getServerAddress(),
                product.getServiceAddress(),
                reviewAddress,
                recommendationAddress);

        return new ProductAggregate(
                product.getProductId(),
                product.getName(),
                product.getWeight(),
                recommendationSummaries,
                reviewSummaries,
                serviceAddresses
        );

    }

    @Override
    public void deleteProduct(int productId) {
        LOG.info("Will delete composite product info for product.id={}", productId);

        productCompositeIntegration.deleteProduct(productId);
        productCompositeIntegration.deleteRecommendations(productId);
        productCompositeIntegration.deleteReviews(productId);
    }
}
