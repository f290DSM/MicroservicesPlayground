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
import dev.sdras.utils.http.HttpErrorInfo;
import dev.sdras.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
public class ProductCompositeIntegration implements ProductService, ReviewService, RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final ObjectMapper mapper;
    private final ProductFeignClient productFeignClient;

    private final String productServiceUrl;
    //TODO: Criar atributos para urls de recomendações e reviews

    @Autowired
    public ProductCompositeIntegration(
            ObjectMapper mapper,
            ProductFeignClient productFeignClient, ServiceUtil serviceUtil,
            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int productServicePort
            //TODO: Carregar os endereços e portas dos serviços de recomendação e review
    ) {
        this.mapper = mapper;
        this.productFeignClient = productFeignClient;
        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
    }

    @Override
    public Product getProduct(Integer productId) {
        try {
            String url = productServiceUrl + productId;
            LOG.debug("Will call getProduct API on URL: {}", url);

            Product product = productFeignClient.getProduct(productId);
            LOG.debug("Found a product with id: {}", product.productId);

            return product;
        } catch (HttpClientErrorException e) {
            switch (Objects.requireNonNull(HttpStatus.resolve(e.getStatusCode().value()))) {
                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(e));
                case UNPROCESSABLE_CONTENT:
                    throw new InvalidInputException(getErrorMessage(e));
                default:
                    LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", e.getStatusCode());
                    LOG.warn("Error body: {}", e.getResponseBodyAsString());
                    throw e;
            }
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        //TODO: Implementar consulta de recomendações
        return List.of();
    }

    @Override
    public List<Review> getReviews(int productId) {
        //TODO: Implementar consulta de reviews
        return List.of();
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
    }
}
