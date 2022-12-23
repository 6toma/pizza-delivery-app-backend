package nl.tudelft.sem.template.store;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import nl.tudelft.sem.template.store.domain.StoreOwnerValidModel;
import org.junit.jupiter.api.Test;

public class StoreOwnerValidModelTest {
    // model1 = model2 Not equal model3
    StoreOwnerValidModel model1 = new StoreOwnerValidModel("email@gmil.com", 1);
    StoreOwnerValidModel model2 = new StoreOwnerValidModel("email@gmil.com", 1);
    StoreOwnerValidModel model3 = new StoreOwnerValidModel("email2@gmil.com", 1);

    @Test
    void testEqual() {
        assertEquals(model1, model2);
        assertNotEquals(model1, model3);
        assertNotEquals(model2, model3);
    }

    @Test
    void testHashCode() {
        assertEquals(model1.hashCode(), model2.hashCode());
        assertNotEquals(model1.hashCode(), model3.hashCode());
    }
}
