package org.example.Amazon;

import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
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
}
