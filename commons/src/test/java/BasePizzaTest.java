import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.junit.jupiter.api.Test;

public class BasePizzaTest {

    private Topping t1 = new Topping("pineapple", 1.5);
    private Topping t2 = new Topping("salami", 1.5);
    private Topping t3 = new Topping("tomatoes", 8.5);

    @Test
    public void testBasePizzaEquals() {
        Pizza pizza2 = new Pizza(1, Arrays.asList(t1, t2));
        Pizza pizza1 = new Pizza(1, Arrays.asList(t1, t2));
        assertEquals(pizza1, pizza2);
    }

    @Test
    public void testBasePizzaEquals1() {
        Pizza pizza2 = new Pizza(1, Arrays.asList(t1, t2, t3));
        Pizza pizza1 = new Pizza(1, Arrays.asList(t3, t1, t2));
        assertEquals(pizza1, pizza2);
    }
}
