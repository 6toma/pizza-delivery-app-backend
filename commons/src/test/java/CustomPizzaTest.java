import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.junit.jupiter.api.Test;

public class CustomPizzaTest {
    private final Topping t1 = new Topping("pineapple", 1.5);
    private final Topping t2 = new Topping("salami", 1.5);
    private final Topping t3 = new Topping("tomatoes", 8.5);

    @Test
    public void testCustomPizzaEq() {
        CustomPizza pizza2 = new CustomPizza("name1", 1, Arrays.asList(t1, t2, t3));
        CustomPizza pizza1 = new CustomPizza("name1", 1, Arrays.asList(t3, t1, t2));
        assertEquals(pizza1, pizza2);
    }

    @Test
    public void testCustomPizzaNotEq() {
        CustomPizza
            pizza2 = new CustomPizza("name1", 1, Arrays.asList(t1, t2, t3));
        CustomPizza
            pizza1 = new CustomPizza("name2", 1, Arrays.asList(t3, t1, t2));
        assertNotEquals(pizza1, pizza2);
    }
}
