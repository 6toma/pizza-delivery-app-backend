import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.junit.jupiter.api.Test;

public class PizzaTest {

    private Topping t1 = new Topping("pineapple", 1.5);
    private Topping t2 = new Topping("salami", 2.5);
    private Topping t3 = new Topping("tomatoes", 8.5);

    private DefaultPizza p1 = new DefaultPizza("hawaii", List.of(t1), 7);
    private DefaultPizza p2 = new DefaultPizza("hawaii", List.of(t2), 7);
    private DefaultPizza p3 = new DefaultPizza("american", List.of(t1, t2), 8.5);


    @Test
    public void getNameTest() {
        assertEquals(p1.getPizzaName(), "hawaii");
    }

    @Test
    public void getPriceTest() {
        assertEquals(p1.getPrice(), 7);
    }

    @Test
    public void getPriceWithToppings() {
        assertEquals(p1.getPrice() + t1.getPrice(), p1.calculatePrice());
        assertEquals(p2.getPrice() + t2.getPrice(), p2.calculatePrice());
        assertEquals(p3.getPrice() + t1.getPrice() + t2.getPrice(), p3.calculatePrice());
    }

    @Test
    public void getToppingsTest() {
        assertEquals(p1.getToppings(), List.of(t1));
    }


    @Test
    public void hashCodeTest() {
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), p3.hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
    }

    @Test
    public void testAddTopping() {
        DefaultPizza temp = new DefaultPizza("hawaii", new ArrayList<>(List.of(t1)), 7);
        temp.addTopping(t2);
        assertEquals(2, temp.getToppings().size());
        assertEquals(temp.getPrice() + t1.getPrice() + t2.getPrice(), temp.calculatePrice());
        assertFalse(temp.addTopping(t2));
    }

    @Test
    public void testRemoveTopping() {
        DefaultPizza temp = new DefaultPizza("hawaii", new ArrayList<>(List.of(t1)), 7);
        temp.removeTopping(t1);
        assertEquals(0, temp.getToppings().size());
        assertEquals(7, temp.calculatePrice());
        assertFalse(temp.removeTopping(t1));
    }





}
