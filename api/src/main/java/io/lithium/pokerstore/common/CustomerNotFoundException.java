package io.lithium.pokerstore.common;

/**
 * Indicate if no customer founded for operation with it
 */
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String id) {
        super(id);
    }

    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerNotFoundException(Throwable cause) {
        super(cause);
    }
}
