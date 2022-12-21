package nl.tudelft.sem.store;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.store.domain.Store;
import org.junit.jupiter.api.Test;

class StoreTest {

    Store store1 = Store.builder().storeId(1).storeName("Store 1").build(); // same id
    Store store2 = Store.builder().storeId(1).storeName("Store 2").build();

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
    void toStringTest() {
        assertThat(store1.toString()).contains("storeId=1");
    }

}