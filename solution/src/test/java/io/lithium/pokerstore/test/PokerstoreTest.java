package io.lithium.pokerstore.test;

import com.orientechnologies.orient.core.Orient;
import io.lithium.pokerstore.PokerstoreFactory;
import io.lithium.pokerstore.backoffice.BackOfficeService;
import io.lithium.pokerstore.common.CustomerNotFoundException;
import io.lithium.pokerstore.common.InsufficientCreditException;
import io.lithium.pokerstore.common.ProductNotFoundException;
import io.lithium.pokerstore.data.Product;
import io.lithium.pokerstore.storefront.StoreFrontService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.Math.toIntExact;
import static junit.framework.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Simple unit test (JUnit 4) for demonstrating a basic program flow
 * of Pokerstore.
 *
 */
public class PokerstoreTest {
    private static final String DELL = "dell-precision-m20";
    private static final String ASUS = "ASUS-n56-vz";
    private static final String MAC = "macbook-pro";

    private static final Product MAC_PRODUCT = new Product(MAC, BigDecimal.valueOf(20.0d), 0);
    private static final Product DELL_PRODUCT = new Product(DELL, BigDecimal.valueOf(10.1d), 0);
    private static final Product ASUS_PRODUCT = new Product(ASUS, BigDecimal.valueOf(12.3), 10);

    private static final String CUSTOMER = "customer";

    private PokerstoreFactory pokerstoreFactory;
    private BackOfficeService backOfficeService;
    private StoreFrontService storeFrontService;

    @Before
    public void setUp() throws Exception {
        pokerstoreFactory = (PokerstoreFactory) Class.forName(
                "io.lithium.pokerstore.PokerstoreFactoryOrientImpl").newInstance();
        // UNCOMMENT to check In-Memory solution
        /*pokerstoreFactory = (PokerstoreFactory) Class.forName(
                "PokerstoreFactoryImpl").newInstance();*/
        backOfficeService = pokerstoreFactory.createBackOfficeService();
        storeFrontService = pokerstoreFactory.createStoreFrontService();
    }

    /**
     * Tight coupling with concrete solution, can be avoided by creating profiles for test or by changing
     * factory interface to support correct shutdown of created `beans`.
     */
    @After
    public void after() {
        Orient.instance().shutdown();
    }

    /**
     * Makes a purchase and tests that inventory and customer credit
     * have been updated accordingly.
     */
    @Test
    public void testNormalFlow() {
        int inventory = 10;
        double price = 1000.0d;
        int quantity = 5;
        double credit = inventory * price * 2;

        stock(DELL, price, inventory);
        setCustomerCredit(credit);

        storeFrontService.purchase(DELL, CUSTOMER, quantity);
        assertEquals(inventory - quantity, backOfficeService.getInventory(DELL));
        assertEquals(credit - (quantity * price), backOfficeService.getCustomerCredit(CUSTOMER));
    }

    /**
     * Makes a purchase by customer with not enough credits, test that purchase not preformed
     */
    @Test
    public void testNoEnoughCredit() {
        prepareStockProducts();

        int quantity = 10;
        // set credit count to make a purchase for only one product
        double customerCredits = MAC_PRODUCT.priceByQuantity(1).doubleValue();
        setCustomerCredit(customerCredits);

        assertThatThrownBy(() -> storeFrontService.purchase(MAC, CUSTOMER, quantity)).isInstanceOf(InsufficientCreditException.class);
        assertEquals(MAC_PRODUCT.getInventory(), backOfficeService.getInventory(MAC));
        assertEquals(customerCredits, backOfficeService.getCustomerCredit(CUSTOMER));
    }

    /**
     * Makes purchase with enough credits but not enough products, test
     */
    @Test
    public void testOutOfStock() {
        prepareStockProducts();

        double credit = 1000.0d;
        setCustomerCredit(credit);

        int expectedOrderQuantity = 1;
        storeFrontService.purchase(ASUS, CUSTOMER, toIntExact(ASUS_PRODUCT.getInventory()) + expectedOrderQuantity);

        double expectedCreditLeft = credit - ASUS_PRODUCT.priceByQuantity(ASUS_PRODUCT.getInventory()).doubleValue();
        assertEquals("Test customer credit left", expectedCreditLeft, backOfficeService.getCustomerCredit(CUSTOMER));
        assertEquals("Test order count after supplier order", expectedOrderQuantity, backOfficeService.getInventory(ASUS));
    }

    /**
     * Makes request by employee to find out of stock products
     */
    @Test
    public void testRequestOutOfStockReport() {
        prepareStockProducts();
        List<Product> outOfStockProducts = backOfficeService.findOutOfStockProducts();
        assertThat(outOfStockProducts).containsExactly(DELL_PRODUCT, MAC_PRODUCT);
    }

    /**
     * Test correct exceptions trowed by not found products
     */
    @Test
    public void testProductNotExist() {
        String notExistedProduct = "HP";
        assertThatThrownBy(() -> backOfficeService.getInventory(notExistedProduct)).isInstanceOf(ProductNotFoundException.class);
    }

    /**
     * Test correct exceptions trowed by not found customer
     */
    @Test
    public void testCustomerNotExist() {
        String notExistedCustomer = "Aliaksei";
        assertThatThrownBy(() -> backOfficeService.getCustomerCredit(notExistedCustomer)).isInstanceOf(CustomerNotFoundException.class);
    }

    /**
     * Sets the customer's credit to the given amount and verifies
     * that it got updated.
     *
     * @param credit amount of money in SEK
     */
    private void setCustomerCredit(double credit) {
        backOfficeService.setCustomerCredit(CUSTOMER, credit);
        assertEquals(credit, backOfficeService.getCustomerCredit(CUSTOMER));
    }

    /**
     * Sets inventory and price for a product and verifies that
     * the levels have been updated.
     *
     * @param productId the product ID
     * @param price     the price in SEK
     * @param inventory the number of items in stock
     */
    private void stock(String productId, double price, long inventory) {
        backOfficeService.stock(productId, price, inventory);
        assertEquals(inventory, backOfficeService.getInventory(productId));
        assertEquals(price, backOfficeService.getPrice(productId));
    }

    /**
     * Perform stock of several products. Utility method to simplify tests
     */
    private void prepareStockProducts() {
        stock(DELL_PRODUCT.getId(), DELL_PRODUCT.getPrice().doubleValue(), DELL_PRODUCT.getInventory());
        stock(ASUS_PRODUCT.getId(), ASUS_PRODUCT.getPrice().doubleValue(), ASUS_PRODUCT.getInventory());
        stock(MAC_PRODUCT.getId(), MAC_PRODUCT.getPrice().doubleValue(), MAC_PRODUCT.getInventory());
    }
}
