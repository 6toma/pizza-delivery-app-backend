import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import nl.tudelft.sem.template.commons.entity.Topping;
import org.junit.jupiter.api.Test;

public class ToppingTest {

    private Topping t1 = new Topping("pineapple", 1.5);
    private Topping t2 = new Topping("pineapple", 1.5);
    private Topping t3 = new Topping("apple", 1.5);

    @Test
    public void getNameTest() {
        assertEquals(t1.getName(), "pineapple");
    }

    @Test
    public void getPriceTest() {
        assertEquals(t1.getPrice(), 1.5);
    }

    @Test
    public void toStringTest() {
        assertEquals(t1.toString(), "pineapple, 1.5");
    }

    @Test
    public void hashCodeTest() {
        assertEquals(t1.hashCode(), t2.hashCode());
        assertNotEquals(t1.hashCode(), t3.hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(t1, t2);
        assertFalse(t1.equals(t3));
    }
}
