package org.example.Barnes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BarnesAndNobleTest {
    @Test
    @DisplayName("specification-based")
    void testNullOrder(){
        BookDatabase db = new BookDatabase() {
            @Override
            public Book findByISBN(String ISBN) {
                return null;
            }
        };
        BuyBookProcess process = new BuyBookProcess() {
            @Override
            public void buyBook(Book book, int amount) {

            }
        };
        BarnesAndNoble barnes = new BarnesAndNoble(db, process);
        PurchaseSummary bn = barnes.getPriceForCart(null);
        assertNull(bn);
    }
    @Test
    @DisplayName("structural-based")
    void testRetrieve(){
        BookDatabase db = new BookDatabase() {
            @Override
            public Book findByISBN(String ISBN) {
                if (ISBN.equals("ISBN1")) {
                    return new Book("ISBN1", 100, 10);
                } else if (ISBN.equals("ISBN2")) {
                    return new Book("ISBN2", 50, 5);
                } else {
                    return null;
                }
            }
        };

        BuyBookProcess process = new BuyBookProcess() {
            @Override
            public void buyBook(Book book, int amount) {}
        };

        BarnesAndNoble barnes = new BarnesAndNoble(db, process);

        Map<String, Integer> order = Map.of(
                "ISBN1", 12,
                "ISBN2", 3
        );
        PurchaseSummary summary = barnes.getPriceForCart(order);
        int expectedTotal = 10 * 100 + 3 * 50;
        assertEquals(expectedTotal, summary.getTotalPrice());
        assertEquals(2, summary.getUnavailable().get(new Book("ISBN1", 100, 10)));
        assertNull(summary.getUnavailable().get(new Book("ISBN2", 50, 5)));
        Book sameBook = new Book("SAME", 10, 1);
        assertTrue(sameBook.equals(sameBook));
    }


}