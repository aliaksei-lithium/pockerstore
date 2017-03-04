package io.lithium.pokerstore.data;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represent domain Customer Entity.
 */
public class Customer {

    private String id;
    private BigDecimal credit;

    public Customer(String id, BigDecimal credit) {
        this.id = id;
        this.credit = credit;
    }

    /**
     * Null object
     */
    public static final Customer NONE = new Customer("", BigDecimal.ZERO);

    public String getId() {
        return id;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) &&
                Objects.equals(credit, customer.credit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, credit);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", credit=" + credit +
                '}';
    }
}

