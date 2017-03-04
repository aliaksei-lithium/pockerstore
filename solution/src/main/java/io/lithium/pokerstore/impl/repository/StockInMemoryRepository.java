package io.lithium.pokerstore.impl.repository;

import io.lithium.pokerstore.data.Product;
import io.lithium.pokerstore.repository.StockRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StockInMemoryRepository implements StockRepository {

    private Map<String, Product> db = new ConcurrentHashMap<>();

    @Override
    public void save(Product product) {
        db.put(product.getId(), product);
    }

    @Override
    public void remove(String id) {
        db.remove(id);
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(db.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(db.values());
    }

    @Override
    public List<Product> findWhere(Predicate<Product> predicate) {
        return db.values().stream().filter(predicate).collect(Collectors.toList());
    }
}
