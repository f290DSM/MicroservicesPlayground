package dev.sdras.microservices.composite.product.services;

import dev.sdras.api.core.product.Product;
import dev.sdras.api.core.product.ProductService;
import dev.sdras.api.core.recommendation.Recommendation;
import dev.sdras.api.core.recommendation.RecommendationService;
import dev.sdras.api.core.review.Review;
import dev.sdras.api.core.review.ReviewService;
import dev.sdras.api.exceptions.InvalidInputException;
import dev.sdras.api.exceptions.NotFoundException;
import dev.sdras.microservices.composite.product.services.feign.ProductFeignClient;
import dev.sdras.microservices.composite.product.services.feign.RecommendationFeignClient;
import dev.sdras.microservices.composite.product.services.feign.ReviewFeignClient;
import dev.sdras.utils.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ProductCompositeIntegration implements ProductService, ReviewService, RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final ObjectMapper mapper;
    private final ProductFeignClient productFeignClient;
    private final RecommendationFeignClient recommendationFeignClient;
    private final ReviewFeignClient reviewFeignClient;

    @Autowired
    public ProductCompositeIntegration(
            ObjectMapper mapper,
            ProductFeignClient productFeignClient,
            RecommendationFeignClient recommendationFeignClient,
            ReviewFeignClient reviewFeignClient
    ) {
        this.mapper = mapper;
        this.productFeignClient = productFeignClient;
        this.recommendationFeignClient = recommendationFeignClient;
        this.reviewFeignClient = reviewFeignClient;
    }

    @Override
    public Product createProduct(Product body) {
        try {
            LOG.debug("Will call createProduct API for productId={}", body.getProductId());
            return productFeignClient.createProduct(body);
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    @Override
    public Product getProduct(int productId) {
        try {
            LOG.debug("Will call getProduct API for productId={}", productId);
            Product product = productFeignClient.getProduct(productId);
            LOG.debug("Found a product with id: {}", product.getProductId());

            return product;
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {
            LOG.debug("Will call deleteProduct API for productId={}", productId);
            productFeignClient.deleteProduct(productId);
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    @Override
    public Recommendation createRecommendation(Recommendation body) {
        try {
            LOG.debug("Will call createRecommendation API for productId={}, recommendationId={}",
                    body.getProductId(),
                    body.getRecommendationId());
            return recommendationFeignClient.createRecommendation(body);
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        try {
            LOG.debug("Will call getRecommendations API for productId={}", productId);
            return recommendationFeignClient.getRecommendations(productId);
        } catch (Exception e) {
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteRecommendations(int productId) {
        try {
            LOG.debug("Will call deleteRecommendations API for productId={}", productId);
            recommendationFeignClient.deleteRecommendations(productId);
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    @Override
    public Review createReview(Review body) {
        try {
            LOG.debug("Will call createReview API for productId={}, reviewId={}",
                    body.getProductId(),
                    body.getReviewId());
            return reviewFeignClient.createReview(body);
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    @Override
    public List<Review> getReviews(int productId) {
        try {
            LOG.debug("Will call getReviews API for productId={}", productId);
            return reviewFeignClient.getReviews(productId);
        } catch (Exception e) {
            LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteReviews(int productId) {
        try {
            LOG.debug("Will call deleteReviews API for productId={}", productId);
            reviewFeignClient.deleteReviews(productId);
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (Objects.requireNonNull(HttpStatus.resolve(ex.getStatusCode().value()))) {
            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));
            case UNPROCESSABLE_ENTITY:
            case UNPROCESSABLE_CONTENT:
                return new InvalidInputException(getErrorMessage(ex));
            default:
                LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
    }
}
