import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PizzaTest {

    private Topping t1 = new Topping("pineapple", 1.5);
    private Topping t2 = new Topping("salami", 1.5);
    private Pizza p1 = new Pizza("hawaii", List.of(t1), 7);
    private Pizza p2 = new Pizza("hawaii", List.of(t1), 7);
    private Pizza p3 = new Pizza("american", List.of(t1, t2), 8.5);


    @Test
    public void getNameTest() {
        assertEquals(p1.getPizzaName(), "hawaii");
    }

    @Test
    public void getPriceTest() {
        assertEquals(p1.getPrice(), 7);
    }

    @Test
    public void getToppingsTest() {
        assertEquals(p1.getToppings(), List.of(t1));
    }

    @Test
    public void toStringTest() {
        assertEquals(p3.toString(), "american;pineapple 1.5;salami 1.5;8.5");
    }

    @Test
    public void hashCodeTest() {
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), p3.hashCode());
    }

    @Test
    public void testEquals() {
        assertTrue(p1.equals(p2));
        assertTrue(!p1.equals(p3));
    }

    @Test
    public void testAddTopping() {
        List<Topping> list = new ArrayList<>();
        list.add(t1);
        Pizza temp = new Pizza("hawaii", list, 7);
        temp.addTopping(t2);
        assertTrue(temp.getToppings().size() == 2);
        assertTrue(temp.getPrice() == 8.5);
        assertFalse(temp.addTopping(t2));
    }

    @Test
    public void testRemoveTopping() {
        List<Topping> list = new ArrayList<>();
        list.add(t1);
        Pizza temp = new Pizza("hawaii", list, 7);
        temp.removeTopping(t1);
        assertTrue(temp.getToppings().size() == 0);
        assertTrue(temp.getPrice() == 5.5);
        assertFalse(temp.removeTopping(t1));
    }
}
