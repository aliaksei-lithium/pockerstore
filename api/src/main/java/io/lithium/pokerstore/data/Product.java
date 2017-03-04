package io.lithium.pokerstore.data;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represent domain Product Entity.
 * Contain all information about product like price and id.
 */
public class Product {

    private String id;
    private BigDecimal price;
    private long inventory;

    public Product(String id, BigDecimal price, long inventory) {
        this.id = id;
        this.price = price;
        this.inventory = inventory;
    }

    /**
     * Null object
     */
    public static final Product NONE = new Product("", BigDecimal.ZERO, -1);


    /**
     * Count product price in depend of requested amount.
     *
     * @param quantity the number of products
     * @return price
     */
    public BigDecimal priceByQuantity(long quantity) {
        return this.price.multiply(BigDecimal.valueOf(quantity));
    }

    public String getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public long getInventory() {
        return inventory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return inventory == product.inventory &&
                Objects.equals(id, product.id) &&
                Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, inventory);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", price=" + price +
                ", inventory=" + inventory +
                '}';
    }
}
