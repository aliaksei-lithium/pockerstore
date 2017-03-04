package io.lithium.pokerstore.storefront;

import io.lithium.pokerstore.common.InsufficientCreditException;

/**
 * High-level business interface for the StoreFront Service. It provides functionality
 * targeted to Pokerstore customers, such as purchasing products.
 *
 */
public interface StoreFrontService {
    /**
     * Makes a purchase of the given quantity of the given product
     * for the given customer. Pokerstore will reorder from the
     * supplier if a product is out of stock.
     *
     * @param productId  the product ID
     * @param customerId the customer ID
     * @param quantity   the number of items to purchase
     * @throws InsufficientCreditException if the customer does not have enough credit
     */
    void purchase(String productId, String customerId, int quantity)
            throws InsufficientCreditException;

}
