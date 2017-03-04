
package io.lithium.pokerstore;

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import io.lithium.pokerstore.backoffice.BackOfficeService;
import io.lithium.pokerstore.backoffice.SupplierService;
import io.lithium.pokerstore.data.Customer;
import io.lithium.pokerstore.data.Product;
import io.lithium.pokerstore.impl.repository.OCustomerRepository;
import io.lithium.pokerstore.impl.repository.OStockRepository;
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
public final class PokerstoreFactoryOrientImpl implements PokerstoreFactory {

    private final CustomerRepository customerRepository;
    private final StockRepository stockRepository;

    private SupplierService supplierService;
    private BackOfficeService backOfficeService;
    private StoreFrontService storeFrontService;

    public PokerstoreFactoryOrientImpl() throws Exception {
        OPartitionedDatabasePool pool = setupOrientDB();
        customerRepository = new OCustomerRepository(pool);
        stockRepository = new OStockRepository(pool);
    }

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

    /**
     * Simple setup of in-memory OrientDB database. Register entity classes
     * and create connection pool.
     *
     * @return created connection pool
     * @throws Exception
     */
    private synchronized OPartitionedDatabasePool setupOrientDB() throws Exception {

        OServer server = OServerMain.create();
        server.startup(PokerstoreFactoryOrientImpl.class.getResourceAsStream("/embedded-server-config.xml"));
        server.activate();

        String username = "admin";
        String password = "admin";
        String dbName = "memory:pockerstore";
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(dbName);
        if (db.exists()) {
            db.open(username, password);
        } else {
            db.create();
        }

        // Register Cluster of domain entities and set indexes
        OClass oProductClass = db.getMetadata().getSchema().getOrCreateClass(Product.class.getSimpleName());
        oProductClass.createProperty("id", OType.STRING);
        oProductClass.createProperty("price", OType.DECIMAL);
        oProductClass.createProperty("inventory", OType.LONG);
        oProductClass.createIndex("Product.id", OClass.INDEX_TYPE.UNIQUE, "id");

        OClass oCustomerClass = db.getMetadata().getSchema().getOrCreateClass(Customer.class.getSimpleName());
        oCustomerClass.createProperty("id", OType.STRING);
        oCustomerClass.createProperty("credit", OType.DECIMAL);
        oCustomerClass.createIndex("Customer.id", OClass.INDEX_TYPE.UNIQUE, "id");

        return new OPartitionedDatabasePool(dbName, username, password);
    }
}
