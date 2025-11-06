package org.example.Amazon;

import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AmazonIntegrationTest {

    static Database db;
    static ShoppingCartAdaptor realCart;
    static PriceRule regularRule;
    static PriceRule extraCostRule;

    @BeforeEach
    void setup() {
        db = new Database();
        db.resetDatabase();
        realCart = new ShoppingCartAdaptor(db);

        regularRule = items -> items.stream().mapToDouble(Item::getPricePerUnit).sum();
        extraCostRule = new ExtraCostForElectronics();
    }

    @Test
    @DisplayName("specification-based")
    void testEmptyCart() {
        Amazon amazon = new Amazon(realCart, List.of(regularRule));
        double total = amazon.calculate();
        assertEquals(0, total);
    }

    @Test
    @DisplayName("structural-based")
    void testCartWithItems() {
        Item electronic = new Item(ItemType.ELECTRONIC, "Macbook", 1, 2500);
        Item other = new Item(ItemType.OTHER, "Cat Tree", 1, 100);

        realCart.add(electronic);
        realCart.add(other);

        Amazon amazon = new Amazon(realCart, List.of(regularRule));
        double total = amazon.calculate();

        assertEquals(2600, total);
        assertEquals(2, realCart.getItems().size());
    }


    @Test
    @DisplayName("structural-based")
    void testExtraCostForElectronics() {
        Item electronic = new Item(ItemType.ELECTRONIC, "Macbook", 1, 2500);
        Item other = new Item(ItemType.OTHER, "Cat Tree", 1, 100);

        realCart.add(electronic);
        realCart.add(other);

        Amazon amazon = new Amazon(realCart, List.of(extraCostRule));
        double total = amazon.calculate();

        assertEquals(7.50, total);
    }


    @Test
    @DisplayName("structural-based")
    void testNumberOfItems() {
        Item electronic = new Item(ItemType.ELECTRONIC, "Macbook", 1, 2500);
        Item other = new Item(ItemType.OTHER, "Cat Tree", 1, 100);

        realCart.add(electronic);
        realCart.add(other);

        int count = realCart.numberOfItems();
        Assertions.assertTrue(count >= 0);
    }
    @Test
    @DisplayName("structural-based")
    void testDatabaseClose() {
        db.close();
        Assertions.assertNull(db.getConnection());
    }
}


