package io.lithium.pokerstore;

import io.lithium.pokerstore.backoffice.BackOfficeService;
import io.lithium.pokerstore.backoffice.SupplierService;
import io.lithium.pokerstore.impl.repository.CustomerInMemoryRepository;
import io.lithium.pokerstore.impl.repository.StockInMemoryRepository;
import io.lithium.pokerstore.impl.service.BackOfficeServiceImpl;
import io.lithium.pokerstore.impl.service.StoreFrontServiceImpl;
import io.lithium.pokerstore.impl.service.SupplierServiceImpl;
import io.lithium.pokerstore.repository.CustomerRepository;
import io.lithium.pokerstore.repository.StockRepository;
import io.lithium.pokerstore.storefront.StoreFrontService;

/**
 * Construct beans for application w/o containers with DI.
 * Fully synchronized for correct infrastructure bean initialization
 */
public final class PokerstoreFactoryImpl implements PokerstoreFactory {

    private final CustomerRepository customerRepository = new CustomerInMemoryRepository();
    private final StockRepository stockRepository = new StockInMemoryRepository();

    private SupplierService supplierService;
    private BackOfficeService backOfficeService;
    private StoreFrontService storeFrontService;

    @Override
    public synchronized BackOfficeService createBackOfficeService() {
        if (backOfficeService == null) {
            backOfficeService = new BackOfficeServiceImpl(stockRepository, customerRepository);
        }
        return backOfficeService;
    }

    @Override
    public synchronized StoreFrontService createStoreFrontService() {
        if (storeFrontService == null) {
            storeFrontService =
                    new StoreFrontServiceImpl(stockRepository, customerRepository, getSupplierService(createBackOfficeService()));
        }
        return storeFrontService;
    }

    private SupplierService getSupplierService(final BackOfficeService backOfficeService) {
        if (supplierService == null) {
            supplierService = new SupplierServiceImpl(backOfficeService);
        }
        return supplierService;
    }
}
