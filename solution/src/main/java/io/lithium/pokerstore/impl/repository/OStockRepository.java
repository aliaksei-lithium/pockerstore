package io.lithium.pokerstore.impl.repository;

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import io.lithium.pokerstore.data.Product;
import io.lithium.pokerstore.repository.ORepository;
import io.lithium.pokerstore.repository.StockRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OStockRepository implements StockRepository, ORepository {

    private final OPartitionedDatabasePool pool;

    public OStockRepository(OPartitionedDatabasePool pool) {
        this.pool = pool;
    }

    @Override
    public Optional<Product> findById(String id) {
        try (ODatabaseDocumentTx db = connection()) {
            List<ODocument> products = findDocumentsById(id, db);
            return products.size() > 0 ? Optional.of(toProduct(products.get(0))) : Optional.empty();
        }
    }

    @Override
    public List<Product> findAll() {
        try (ODatabaseDocumentTx db = connection()) {
            List<ODocument> products = toList(db.browseClass(Product.class.getSimpleName()));
            return toProductList(products);
        }
    }

    @Override
    public List<Product> findWhere(Predicate<Product> predicate) {
        try (ODatabaseDocumentTx db = connection()) {
            List<ODocument> products = toList(db.browseClass(Product.class.getSimpleName()));
            return toProductList(products).stream().filter(predicate).collect(Collectors.toList());
        }
    }

    @Override
    public void save(Product product) {
        try (ODatabaseDocumentTx db = connection()) {
            ODocument oProduct = toDocument(product);
            db.command(new OCommandSQL("UPDATE Product " +
                                               "set id = :id, price = :price, inventory = :inventory " +
                                               "UPSERT where id = :id")).execute(oProduct.toMap());
            db.commit();
        }
    }

    @Override
    public void remove(String id) {
        try (ODatabaseDocumentTx db = connection()) {
            List<ODocument> products = findDocumentsById(id, db);
            products.stream().forEach(ODocument::delete);
            db.commit();
        }
    }

    private ODatabaseDocumentTx connection() {
        return pool.acquire();
    }

    private List<ODocument> findDocumentsById(String id, ODatabaseDocumentTx db) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return db.command(new OSQLSynchQuery<ODocument>("SELECT FROM Product where id = :id"))
                 .execute(params);
    }

    /**
     * Mapper utility method. Construct new Product from ODocument.
     *
     * @param productDoc document from Product cluster.
     * @return created Product object.
     */
    private Product toProduct(ODocument productDoc) {
        return new Product(productDoc.field("id"), productDoc.field("price"), productDoc.field("inventory"));
    }

    /**
     * Mapper utility method. Make ODocument from Product entity object.
     *
     * @param product product entity
     * @return database document from Product cluster
     */
    private ODocument toDocument(Product product) {
        ODocument oProduct = new ODocument(Product.class.getSimpleName());
        oProduct.field("id", product.getId());
        oProduct.field("price", product.getPrice());
        oProduct.field("inventory", product.getInventory());
        return oProduct;

    }

    private List<Product> toProductList(List<ODocument> productDocList) {
        return productDocList.stream().map(this::toProduct).collect(Collectors.toList());
    }
}
