package nl.tudelft.sem.template.checkout;

import nl.tudelft.sem.template.checkout.domain.PickupTimeConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class PickupTimeConfigTest {

    @Test
    public void test_not_null() {
        PickupTimeConfig pickupTimeConfig = new PickupTimeConfig();

        Assertions.assertThat(pickupTimeConfig.jackson2ObjectMapperBuilderCustomizer()).isNotNull();
    }
}
