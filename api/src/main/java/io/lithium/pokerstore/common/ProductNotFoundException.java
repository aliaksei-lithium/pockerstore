package io.lithium.pokerstore.common;

/**
 * Indicate if no product founded for operation with it
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String id) {
        super(id);
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductNotFoundException(Throwable cause) {
        super(cause);
    }
}
