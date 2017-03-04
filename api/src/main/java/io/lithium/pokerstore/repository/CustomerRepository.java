package io.lithium.pokerstore.repository;

import io.lithium.pokerstore.data.Customer;

import java.util.Optional;

/**
 * Access layer to handle requests for Customer entity
 */
public interface CustomerRepository {

    /**
     * Perform Customer lookup by id
     * @param id Customer id
     * @return Customer founded by id
     */
    Optional<Customer> findById(String id);

    /**
     * Persist Customer entity in repository
     * @param customer customer to save
     */
    void save(Customer customer);
}
