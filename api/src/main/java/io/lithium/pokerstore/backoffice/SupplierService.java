package io.lithium.pokerstore.backoffice;

/**
 * Provide functionality for product supply, like ordering a new one.
 *
 */
public interface SupplierService {

    /**
     * Perform an order for custom supplier provider of new product
     *
     * @param productId the product ID
     * @param quantity number on products to order
     */
    void order(String productId, long quantity);
}
