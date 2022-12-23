import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.commons.entity.Cart;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.junit.jupiter.api.Test;

public class CartTest {

    private Topping t1 = new Topping("pineapple", 1.5);
    private Topping t2 = new Topping("salami", 1.5);
    private CustomPizza p1 = new CustomPizza("hawaii", 7, List.of(t1));
    private CustomPizza p2 = new CustomPizza("hawaii", 7, List.of(t1));
    private Cart c1 = new Cart(new NetId("me@test.com"), new HashMap<>());


    @Test
    public void addPizzaTest() {
        c1.addPizza(p1);
        assertEquals(c1.getPizzasMap().size(), 1);
        c1.addPizza(p2);
        assertEquals(c1.getPizzasMap().get(p2), 2);
    }

    @Test
    public void removePizzaTest() {
        c1.addPizza(p1);
        c1.removePizza(p1);
        assertEquals(c1.getPizzasMap().size(), 0);
    }

}
