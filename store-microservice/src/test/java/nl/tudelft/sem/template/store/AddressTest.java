package nl.tudelft.sem.template.store;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import nl.tudelft.sem.template.store.domain.StoreAddress;
import org.junit.jupiter.api.Test;


public class AddressTest {
    private final StoreAddress storeAddress = new StoreAddress("zipCode", "some street", 234);

    @Test
    void testGetters() {
        assertThat(storeAddress.getNumber()).isEqualTo(234);
        assertThat(storeAddress.getStreet()).isEqualTo("some street");
        assertThat(storeAddress.getZipCode()).isEqualTo("zipCode");
    }

    @Test
    void toStringTest() {
        assertThat(storeAddress.toString())
                .containsOnlyOnce("zipCode=" + storeAddress.getZipCode())
                .containsOnlyOnce("street=" + storeAddress.getStreet())
                .containsOnlyOnce("number=" + storeAddress.getNumber());
    }
}
