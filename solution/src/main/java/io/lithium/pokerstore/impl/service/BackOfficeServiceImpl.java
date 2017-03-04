package io.lithium.pokerstore.impl.service;

import io.lithium.pokerstore.backoffice.BackOfficeService;
import io.lithium.pokerstore.common.CustomerNotFoundException;
import io.lithium.pokerstore.common.ProductNotFoundException;
import io.lithium.pokerstore.data.Customer;
import io.lithium.pokerstore.data.Product;
import io.lithium.pokerstore.impl.lock.NaiveStripeLock;
import io.lithium.pokerstore.lock.LockName;
import io.lithium.pokerstore.lock.StripeLock;
import io.lithium.pokerstore.repository.CustomerRepository;
import io.lithium.pokerstore.repository.StockRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;

public class BackOfficeServiceImpl implements BackOfficeService {

    private final StockRepository stockRepository;
    private final CustomerRepository customerRepository;

    private final StripeLock locker;

    public BackOfficeServiceImpl(final StockRepository stockRepository, final CustomerRepository customerRepository) {
        this.stockRepository = stockRepository;
        this.customerRepository = customerRepository;

        this.locker = NaiveStripeLock.INSTANCE;
    }

    @Override
    public void stock(final String productId, double price, long inventory) {
        ReadWriteLock lock = locker.acquireWrite(LockName.PRODUCT);
        try {
            stockRepository.save(new Product(productId, BigDecimal.valueOf(price), inventory));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void setCustomerCredit(final String customerId, double credit) {
        ReadWriteLock lock = locker.acquireWrite(LockName.CUSTOMER);
        try {
            customerRepository.save(new Customer(customerId, BigDecimal.valueOf(credit)));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public long getInventory(final String productId) throws ProductNotFoundException {
        return findProductById(productId).getInventory();
    }

    @Override
    public double getPrice(final String productId) throws ProductNotFoundException {
        return findProductById(productId).getPrice().doubleValue();
    }

    @Override
    public double getCustomerCredit(final String customerId) throws CustomerNotFoundException {
        return findCustomerById(customerId).getCredit().doubleValue();
    }

    @Override
    public List<Product> findOutOfStockProducts() {
        return stockRepository.findWhere(product -> product.getInventory() == 0);
    }

    private Product findProductById(final String id) throws ProductNotFoundException {
        ReadWriteLock lock = locker.acquireRead(LockName.PRODUCT);
        try {
            Optional<Product> product = stockRepository.findById(id);
            return product.orElseThrow(() -> new ProductNotFoundException(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    private Customer findCustomerById(final String id) throws CustomerNotFoundException {
        ReadWriteLock lock = locker.acquireRead(LockName.CUSTOMER);
        try {
            Optional<Customer> customer = customerRepository.findById(id);
            return customer.orElseThrow(() -> new CustomerNotFoundException(id));
        } finally {
            lock.readLock().unlock();
        }
    }
}
