package nl.tudelft.sem.template.example;

import nl.tudelft.sem.store.Store;
import nl.tudelft.sem.store.StoreAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StoreTest {

    Store store1 = new Store(1, new StoreAddress("zipCode", "some street", 234));
    Store store2 = new Store(1, new StoreAddress("zipCode", "other street", 234));

    @Test
    void testEquals() {
        assertEquals(store1, store1);
        assertEquals(store2, store2, "Should only compare storeId");
    }

    @Test
    void testHashCode() {
        assertEquals(store1.hashCode(), store2.hashCode(), "Should only hash storeId");
    }

    @Test
    void testToString() {
        System.out.println(store1.toString());
    }
}