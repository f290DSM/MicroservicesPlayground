package dev.sdras.microservices.composite.product.services;

import dev.sdras.api.composite.product.ProductAggregate;
import dev.sdras.api.composite.product.ProductCompositeService;
import dev.sdras.api.composite.product.ServiceAddresses;
import dev.sdras.api.core.product.Product;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class ProductServiceCompositeImpl implements ProductCompositeService {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ProductServiceCompositeImpl.class);

    private final ProductCompositeIntegration productCompositeIntegration;

    public ProductServiceCompositeImpl(ProductCompositeIntegration productCompositeIntegration) {
        this.productCompositeIntegration = productCompositeIntegration;
    }

    @Override
    public ProductAggregate getProduct(int productId) {
        LOG.info("Will get composite product info for product.id={}", productId);

        Product product = productCompositeIntegration.getProduct(productId);

        ServiceAddresses serviceAddresses = new ServiceAddresses("",
                product.serviceAddress,
                "",
                ""
        );

        return new ProductAggregate(
                product.productId,
                product.name,
                product.weight,
                Collections.emptyList(),
                Collections.emptyList(),
                serviceAddresses
        );

    }
}
