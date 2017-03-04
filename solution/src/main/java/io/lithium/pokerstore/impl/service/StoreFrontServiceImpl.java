package io.lithium.pokerstore.impl.service;

import io.lithium.pokerstore.backoffice.SupplierService;
import io.lithium.pokerstore.common.InsufficientCreditException;
import io.lithium.pokerstore.data.Customer;
import io.lithium.pokerstore.data.Product;
import io.lithium.pokerstore.impl.lock.NaiveStripeLock;
import io.lithium.pokerstore.lock.StripeLock;
import io.lithium.pokerstore.repository.CustomerRepository;
import io.lithium.pokerstore.repository.StockRepository;
import io.lithium.pokerstore.storefront.StoreFrontService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;

import static io.lithium.pokerstore.lock.LockName.PRODUCT;

public final class StoreFrontServiceImpl implements StoreFrontService {

    private final StockRepository stockRepository;
    private final CustomerRepository customerRepository;
    private final SupplierService supplierService;

    private final StripeLock locker;

    public StoreFrontServiceImpl(final StockRepository stockRepository, final CustomerRepository customerRepository,
                                 final SupplierService supplierService) {
        this.stockRepository = stockRepository;
        this.customerRepository = customerRepository;
        this.supplierService = supplierService;

        locker = NaiveStripeLock.INSTANCE;
    }

    @Override
    public void purchase(final String productId, final String customerId, int quantity) throws InsufficientCreditException {
        ReadWriteLock lock = locker.acquireWrite(PRODUCT);
        try {
            Optional<Product> product = stockRepository.findById(productId);
            Optional<Customer> customer = customerRepository.findById(customerId);

            if (product.isPresent() && customer.isPresent()) {
                long customerOrderQuantityLeft = performPurchase(product.get(), customer.get(), quantity);

                if (customerOrderQuantityLeft > 0) {
                    supplierService.order(productId, customerOrderQuantityLeft);
                }
            } else {
                throw new RuntimeException(
                        String.format("Purchase can't be made for Product [id: %s, exist: %b] by Customer [id: %s, exist: %b].",
                                productId, product.isPresent(), customerId, customer.isPresent()));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private long performPurchase(final Product product, final Customer customer, int quantity) {
        BigDecimal creditAmount = product.priceByQuantity(quantity);

        if (isCustomerEligibleToPurchase(customer, creditAmount)) {
            long productInventoryLeft;
            long customerOrderQuantityLeft;

            if (product.getInventory() >= quantity) {
                productInventoryLeft = product.getInventory() - quantity;
                customerOrderQuantityLeft = 0;
            } else {
                productInventoryLeft = 0;
                customerOrderQuantityLeft = quantity - product.getInventory();
            }

            persistPurchaseChanges(
                    new Product(product.getId(), product.getPrice(), productInventoryLeft),
                    new Customer(customer.getId(), customer.getCredit().subtract(product.priceByQuantity(quantity - customerOrderQuantityLeft)))
            );

            return customerOrderQuantityLeft;
        } else {
            throw new InsufficientCreditException(String.format("Customer [id: %s, credit: %s, quantity: %s] have not enough credit for Product [id: %s, price: %s]",
                    customer.getId(), customer.getCredit().doubleValue(), quantity, product.getId(), product.getPrice().doubleValue()));
        }
    }

    private boolean isCustomerEligibleToPurchase(final Customer customer, final BigDecimal creditAmount) {
        return customer.getCredit().compareTo(creditAmount) >= 0;
    }

    private void persistPurchaseChanges(final Product product, Customer customer) {
        stockRepository.save(product);
        customerRepository.save(customer);
    }
}
