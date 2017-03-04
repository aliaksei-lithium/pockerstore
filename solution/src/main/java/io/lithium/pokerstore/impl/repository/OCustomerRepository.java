package io.lithium.pokerstore.impl.repository;

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import io.lithium.pokerstore.data.Customer;
import io.lithium.pokerstore.repository.CustomerRepository;
import io.lithium.pokerstore.repository.ORepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OCustomerRepository implements CustomerRepository, ORepository {

    private final OPartitionedDatabasePool pool;

    public OCustomerRepository(OPartitionedDatabasePool pool) {
        this.pool = pool;
    }

    @Override
    public Optional<Customer> findById(String id) {
        try (ODatabaseDocumentTx db = connection()) {
            List<ODocument> customers = findDocumentsById(id, db);
            return customers.size() > 0 ? Optional.of(toCustomer(customers.get(0))) : Optional.empty();
        }
    }

    @Override
    public void save(Customer customer) {
        try (ODatabaseDocumentTx db = connection()) {
            ODocument oCustomer = toDocument(customer);
            db.command(new OCommandSQL("UPDATE Customer " +
                                               "set id = :id, credit = :credit " +
                                               "UPSERT where id = :id")).execute(oCustomer.toMap());
            db.commit();
        }
    }

    private ODatabaseDocumentTx connection() {
        return pool.acquire();
    }

    private List<ODocument> findDocumentsById(String id, ODatabaseDocumentTx db) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return db.command(new OSQLSynchQuery<ODocument>("SELECT FROM Customer where id = :id"))
                 .execute(params);
    }

    /**
     * Mapper utility method. Make ODocument from Customer entity
     *
     * @param customer Customer object entity
     * @return database document from Customer cluster
     */
    private ODocument toDocument(Customer customer) {
        ODocument oCustomer = new ODocument(Customer.class.getSimpleName());
        oCustomer.field("id", customer.getId());
        oCustomer.field("credit", customer.getCredit());
        return oCustomer;

    }

    /**
     * Mapper utility method. Construct new Customer object from ODocument.
     *
     * @param customerDoc document from Customer cluster.
     * @return created customer object.
     */
    private Customer toCustomer(ODocument customerDoc) {
        return new Customer(customerDoc.field("id"), customerDoc.field("credit"));
    }

}
