package io.lithium.pokerstore.impl.repository;

import io.lithium.pokerstore.data.Customer;
import io.lithium.pokerstore.repository.CustomerRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerInMemoryRepository implements CustomerRepository {

    private Map<String, Customer> db = new ConcurrentHashMap<>();

    @Override
    public Optional<Customer> findById(String id) {
        return Optional.ofNullable(db.get(id));
    }

    @Override
    public void save(Customer customer) {
        db.put(customer.getId(), customer);
    }
}
