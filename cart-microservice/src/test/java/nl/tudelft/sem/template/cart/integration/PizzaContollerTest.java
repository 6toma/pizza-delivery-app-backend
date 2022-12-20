package nl.tudelft.sem.template.cart.integration;

import nl.tudelft.sem.template.cart.PizzaService;
import nl.tudelft.sem.template.cart.ToppingService;
import nl.tudelft.sem.template.cart.controllers.PizzaController;
import nl.tudelft.sem.template.cart.controllers.ToppingController;
import nl.tudelft.sem.template.cart.exceptions.PizzaNameAlreadyInUseException;
import nl.tudelft.sem.template.cart.exceptions.ToppingAlreadyInUseException;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.PizzaModel;
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

public class PizzaContollerTest {

    private Topping t1 = new Topping("pineapple", 1.5);
    private DefaultPizza p1 = new DefaultPizza("hawaii", List.of(t1), 6);
    private PizzaService ps;
    private PizzaController pc;
    private PizzaModel pm = new PizzaModel();

    @BeforeEach
    void beforeEach() {
        ps = Mockito.mock(PizzaService.class);
        pc = new PizzaController(ps);
        pm.setPizzaName("hawaii");
        pm.setPrice(6);
        pm.setToppings(List.of(t1));
    }

    @Test
    public void addPizzaTest() throws Exception {
        Mockito.when(ps.addPizza("hawaii", List.of(t1), 6)).thenReturn(p1);
        assertEquals(pc.addPizza(pm).getBody(), "Pizza added");
    }

    @Test
    public void removePizzaTest() throws Exception {
        doNothing().when(ps).removePizza("hawaii");
        assertEquals(pc.removePizza("hawaii").getBody(), "Pizza removed");
    }

    @Test
    public void editPizzaTest() throws Exception {
        doNothing().when(ps).editPizza("hawaii", List.of(t1), 6);
        assertEquals(pc.editPizza(pm).getBody(), "Pizza edited");
    }

    @Test
    public void addPizzaTestException() throws Exception {
        Mockito.when(ps.addPizza("hawaii", List.of(t1), 6)).thenThrow(new PizzaNameAlreadyInUseException("hawaii"));
        assertThrows(ResponseStatusException.class, () ->  {
            pc.addPizza(pm);
        });
    }

    @Test
    public void removePizzaTestException() throws Exception {
        doThrow(new ToppingAlreadyInUseException("pineapple")).when(ps).removePizza("hawaii");
        assertThrows(ResponseStatusException.class, () ->  {
            pc.removePizza("hawaii");
        });
    }

    @Test
    public void getPizzasTest() {
        Mockito.when(ps.getAll()).thenReturn(List.of(p1));
        assertEquals(pc.getPizzas(), List.of(p1));
    }
}
