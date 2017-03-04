package io.lithium.pokerstore.impl.service;

import io.lithium.pokerstore.backoffice.BackOfficeService;
import io.lithium.pokerstore.backoffice.SupplierService;

public class SupplierServiceImpl implements SupplierService {

    private final BackOfficeService backOfficeService;

    public SupplierServiceImpl(final BackOfficeService backOfficeService) {
        this.backOfficeService = backOfficeService;
    }

    /**
     * Make direct stock of ordered product.
     *
     * @param productId the product ID
     * @param quantity number on products to order
     */
    @Override
    public void order(final String productId, long quantity) {
        backOfficeService.stock(productId, backOfficeService.getPrice(productId), quantity);
    }
}
