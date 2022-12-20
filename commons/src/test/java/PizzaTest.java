import java.util.Arrays;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PizzaTest {

    private Topping t1 = new Topping("pineapple", 1.5);
    private Topping t2 = new Topping("salami", 1.5);
    private Topping t3 = new Topping("tomatoes", 8.5);

    private DefaultPizza p1 = new DefaultPizza("hawaii", List.of(t1), 7);
    private DefaultPizza p2 = new DefaultPizza("hawaii", List.of(t1), 7);
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
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
    }

    @Test
    public void testAddTopping() {
        List<Topping> list = new ArrayList<>();
        list.add(t1);
        DefaultPizza temp = new DefaultPizza("hawaii", list, 7);
        temp.addTopping(t2);
        assertEquals(2, temp.getToppings().size());
        assertEquals(8.5, temp.getPrice());
        assertFalse(temp.addTopping(t2));
    }

    @Test
    public void testRemoveTopping() {
        List<Topping> list = new ArrayList<>();
        list.add(t1);
        DefaultPizza temp = new DefaultPizza("hawaii", list, 7);
        temp.removeTopping(t1);
        assertEquals(0, temp.getToppings().size());
        assertEquals(5.5, temp.getPrice());
        assertFalse(temp.removeTopping(t1));
    }





}
