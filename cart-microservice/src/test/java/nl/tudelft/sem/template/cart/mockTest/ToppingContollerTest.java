package nl.tudelft.sem.template.cart.mockTest;

import nl.tudelft.sem.template.cart.ToppingRepository;
import nl.tudelft.sem.template.cart.ToppingService;
import nl.tudelft.sem.template.cart.controllers.ToppingController;
import nl.tudelft.sem.template.cart.exceptions.ToppingAlreadyInUseException;
import nl.tudelft.sem.template.cart.exceptions.ToppingNotFoundException;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.ToppingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

public class ToppingContollerTest {

    private Topping t1 = new Topping("pineapple", 1.5);
    private ToppingService ts;
    private ToppingController tc;
    private ToppingModel tm = new ToppingModel();

    @BeforeEach
    void beforeEach() {
        ts = Mockito.mock(ToppingService.class);
        tc = new ToppingController(ts);
        tm.setName("pineapple");
        tm.setPrice(1.5);
    }

    @Test
    public void addToppingTest() throws Exception {
        Mockito.when(ts.addTopping("pineapple", 1.5)).thenReturn(t1);
        assertEquals(tc.addTopping(tm).getBody(), "Topping added");
    }

    @Test
    public void removeToppingTest() throws Exception {
        doNothing().when(ts).removeTopping("pineapple");
        assertEquals(tc.removeTopping("pineapple").getBody(), "Topping removed");
    }

    @Test
    public void editToppingTest() throws ToppingNotFoundException {
        doNothing().when(ts).editTopping("pineapple", 1.5);
        assertEquals(tc.editTopping(tm).getBody(), "Topping edited");
    }

    @Test
    public void addToppingTestException() throws Exception {
        Mockito.when(ts.addTopping("pineapple", 1.5)).thenThrow(new ToppingAlreadyInUseException("pineapple"));
        assertThrows(ResponseStatusException.class, () ->  {
            tc.addTopping(tm);
        });
    }

    @Test
    public void removeToppingTestException() throws Exception {
        doThrow(new ToppingAlreadyInUseException("pineapple")).when(ts).removeTopping("pineapple");
        assertThrows(ResponseStatusException.class, () ->  {
            tc.removeTopping("pineapple");
        });
    }

    @Test
    public void getToppingsTest() {
        Mockito.when(ts.getAll()).thenReturn(List.of(t1));
        assertEquals(tc.getToppings(), List.of(t1));
    }
}
