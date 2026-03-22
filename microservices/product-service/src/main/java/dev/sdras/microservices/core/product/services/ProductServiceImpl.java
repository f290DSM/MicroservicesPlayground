package dev.sdras.microservices.core.product.services;

import dev.sdras.api.core.product.Product;
import dev.sdras.api.core.product.ProductService;
import dev.sdras.utils.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ServiceUtil serviceUtil;

    public ProductServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Product getProduct(Integer productId) {
        LOG.debug("/product return the found product for productId={}", productId);
        return new Product(productId, "Product: " + productId, new Random().nextInt(2000), serviceUtil.getServerAddress());
    }
}
