package org.example.Amazon;

import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AmazonUnitTest {

    static ShoppingCart mockCart;
    static Item electronicItem;
    static Item otherItem;
    static PriceRule mockRule;

    @BeforeAll
    static void setup() {
        mockCart = mock(ShoppingCart.class);
        electronicItem = new Item(ItemType.ELECTRONIC, "Macbook", 1, 2500);
        otherItem = new Item(ItemType.OTHER, "Cat Tree", 1, 100);
        when(mockCart.getItems()).thenReturn(List.of(electronicItem, otherItem));

        mockRule = mock(PriceRule.class);
        when(mockRule.priceToAggregate(anyList())).thenAnswer(invocation -> {
            List<Item> items = invocation.getArgument(0);
            return items.stream().mapToDouble(Item::getPricePerUnit).sum();
        });
    }

    @Test
    @DisplayName("specification-based")
    void testEmpty() {
        ShoppingCart emptyCart = mock(ShoppingCart.class);
        when(emptyCart.getItems()).thenReturn(List.of());
        Amazon amazon = new Amazon(emptyCart, List.of(mockRule));
        double total = amazon.calculate();
        assertEquals(0, total);
    }

    @Test
    @DisplayName("structural-based")
    void testValid() {
        Amazon amazon = new Amazon(mockCart, List.of(mockRule));
        double total = amazon.calculate();
        assertEquals(2600, total);

        Item newItem = new Item(ItemType.ELECTRONIC, "iPad", 1, 800);
        amazon.addToCart(newItem);
        verify(mockCart).add(newItem);
    }
    @Test
    @DisplayName("item coverage")
    void testItemMethods() {
        assertEquals(ItemType.ELECTRONIC, electronicItem.getType());
        assertEquals("Macbook", electronicItem.getName());
        assertEquals(1, electronicItem.getQuantity());
        assertEquals(2500, electronicItem.getPricePerUnit());

        assertTrue(electronicItem.equals(electronicItem));
        assertFalse(electronicItem.equals(otherItem));
    }
    @Test
    @DisplayName("specification-based")
    void testExtraCostRule() {
        ExtraCostForElectronics rule = new ExtraCostForElectronics();
        Item electronic = new Item(ItemType.ELECTRONIC, "Macbook", 1, 2500);
        Item other = new Item(ItemType.OTHER, "Cat Tree", 1, 100);

        double total = rule.priceToAggregate(List.of(electronic, other));
        assertEquals(7.50, total);

        total = rule.priceToAggregate(List.of(other));
        assertEquals(0, total);
    }

    @Test
    @DisplayName("specification-based DeliveryPrice")
    void testDeliveryPrice() {
        DeliveryPrice rule = new DeliveryPrice();

        double totalWithElectronic = rule.priceToAggregate(List.of(electronicItem));
        assertEquals(5, totalWithElectronic);

        double totalWithOther = rule.priceToAggregate(List.of(otherItem));
        assertEquals(5, totalWithOther);

        double totalWith3Items = rule.priceToAggregate(List.of(electronicItem, otherItem, electronicItem));
        assertEquals(5, totalWith3Items);

        double totalWith5Items = rule.priceToAggregate(List.of(electronicItem, otherItem, electronicItem, otherItem, electronicItem));
        assertEquals(12.5, totalWith5Items);

        List<Item> manyItems = new java.util.ArrayList<>();
        for (int i = 0; i < 11; i++) {
            manyItems.add(electronicItem);
        }
        assertEquals(20.0, rule.priceToAggregate(manyItems));
    }


}
