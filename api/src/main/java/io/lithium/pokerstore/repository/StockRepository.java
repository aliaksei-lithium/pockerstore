package io.lithium.pokerstore.repository;

import io.lithium.pokerstore.data.Product;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Access layer to handle requests for Product entity
 */
public interface StockRepository {

    /**
     * Find Product by id and return Optional value.
     * Optional is more preferable for interface and can be more expressive implemented in
     * different implementations
     *
     * @param id id of Product that need to get from repository
     *
     * @return product founded by id
     */
    Optional<Product> findById(String id);

    /**
     * Find all Product
     *
     * @return all products in repository
     */
    List<Product> findAll();

    /**
     * General purpose method for Product request with predicate.
     *
     * @param predicate filter function
     *
     * @return filtered list of Products by predicate
     */
    List<Product> findWhere(Predicate<Product> predicate);

    /**
     * Persist Product entity in repository.
     *
     * @param product product to save
     */
    void save(Product product);

    /**
     * Remove product by id.
     *
     * @param id product id to remove
     */
    void remove(String id);
}
