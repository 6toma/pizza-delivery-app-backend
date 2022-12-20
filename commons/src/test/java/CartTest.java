import java.util.Arrays;
import java.util.HashMap;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.commons.entity.Cart;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CartTest {

    private Topping t1 = new Topping("pineapple", 1.5);
    private Topping t2 = new Topping("salami", 1.5);
    private CustomPizza p1 = new CustomPizza("hawaii", 7, List.of(t1));
    private CustomPizza p2 = new CustomPizza("hawaii", 7, List.of(t1));
    private Cart c1 = new Cart(new NetId("me"), new HashMap<>());


    @Test
    public void addPizzaTest() {
        c1.addPizza(p1);
        assertEquals(c1.getPizzasMap().size(), 1);
        c1.addPizza(p2);
        assertEquals(c1.getPizzasMap().get(p2), 2);
    }

    public void removePizzaTest() {
        c1.addPizza(p1);
        c1.removePizza(p1);
        assertEquals(c1.getPizzasMap().size(), 0);
    }

    /*public void addToppingTest() {
        c1.addPizza(p1);
        c1.addTopping(p1, t2);
        assertEquals(c1.getPizzasMap().get(0).getPrice(), 8.5);
        assertEquals(c1.getPizzasMap().get(0).getToppings().size(), 2);
        assertEquals(c1.getPizzasMap().size(), 1);
        assertFalse(c1.addTopping(p1, t2));
    }

    public void removeToppingTest() {
        c1.addPizza(p1);
        c1.removeTopping(p1, t1);
        assertEquals(c1.getPizzasMap().get(0).getPrice(), 5.5);
        assertEquals(c1.getPizzasMap().get(0).getToppings().size(), 2);
        assertEquals(c1.getPizzasMap().size(), 1);
        assertFalse(c1.removeTopping(p1, t1));
    }*/
}