package nl.tudelft.sem.template.cart.mockTest;

import nl.tudelft.sem.template.cart.exceptions.PizzaNameNotFoundException;
import nl.tudelft.sem.template.cart.services.PizzaService;
import nl.tudelft.sem.template.cart.controllers.PizzaController;
import nl.tudelft.sem.template.cart.exceptions.PizzaNameAlreadyInUseException;
import nl.tudelft.sem.template.cart.exceptions.ToppingAlreadyInUseException;
import nl.tudelft.sem.template.cart.services.ToppingService;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.PizzaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

public class PizzaContollerTest {

    private Topping t1 = new Topping("pineapple", 1.5);
    private DefaultPizza p1 = new DefaultPizza("hawaii", List.of(t1), 6);
    private PizzaService ps;
    private ToppingService toppingService;
    private PizzaController pc;
    private PizzaModel pm = new PizzaModel();

    @BeforeEach
    void beforeEach() {
        ps = Mockito.mock(PizzaService.class);
        toppingService = Mockito.mock(ToppingService.class);
        pc = new PizzaController(ps, toppingService);
        pm.setPizzaName("hawaii");
        pm.setPrice(6);
        pm.setToppings(List.of(t1.getName()));
    }

    @Test
    public void addPizzaTest() throws Exception {
        Mockito.when(ps.addPizza("hawaii", List.of(t1), 6)).thenReturn(p1);
        Mockito.when(toppingService.findAllByNames(any())).thenReturn(List.of(t1));
        assertThat(pc.addPizza(pm).getBody()).startsWith("Pizza added");
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
        Mockito.when(ps.addPizza(any(), any(), anyDouble())).thenThrow(new PizzaNameAlreadyInUseException("hawaii"));
        assertThrows(ResponseStatusException.class, () -> pc.addPizza(pm));
    }

    @Test
    public void removePizzaTestException() throws Exception {
        doThrow(new PizzaNameNotFoundException("hawaii")).when(ps).removePizza("hawaii");
        assertThrows(ResponseStatusException.class, () -> pc.removePizza("hawaii"));
    }

    @Test
    public void getPizzasTest() {
        Mockito.when(ps.getAll()).thenReturn(List.of(p1));
        assertEquals(pc.getPizzas(), List.of(p1));
    }
}
