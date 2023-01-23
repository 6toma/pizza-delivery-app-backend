package nl.tudelft.sem.template.cart.mocktest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.cart.controllers.PizzaController;
import nl.tudelft.sem.template.cart.exceptions.PizzaNameAlreadyInUseException;
import nl.tudelft.sem.template.cart.exceptions.PizzaNameNotFoundException;
import nl.tudelft.sem.template.cart.services.PizzaService;
import nl.tudelft.sem.template.cart.services.ToppingService;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.PizzaModel;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import nl.tudelft.sem.template.commons.utils.RequestObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ResponseStatusException;

public class PizzaContollerTest {

    private Topping t1 = new Topping("pineapple", 1.5);
    private DefaultPizza p1 = new DefaultPizza("hawaii", List.of(t1), 6);
    private PizzaService ps;
    private ToppingService toppingService;
    private RequestHelper requestHelper;
    private AuthManager authManager;
    private PizzaController pc;
    private PizzaModel pm = new PizzaModel();

    @BeforeEach
    void beforeEach() {
        ps = Mockito.mock(PizzaService.class);
        toppingService = Mockito.mock(ToppingService.class);
        requestHelper = Mockito.mock(RequestHelper.class);
        authManager = Mockito.mock(AuthManager.class);
        pc = new PizzaController(ps, toppingService, requestHelper, authManager);
        pm.setPizzaName("hawaii");
        pm.setPrice(6);
        pm.setToppings(List.of(t1.getName()));
    }

    @Test
    public void addPizzaTest() throws Exception {
        when(ps.addPizza("hawaii", List.of(t1), 6)).thenReturn(p1);
        when(toppingService.findAllByNames(any())).thenReturn(List.of(t1));
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
        when(ps.addPizza(any(), any(), anyDouble())).thenThrow(new PizzaNameAlreadyInUseException("hawaii"));
        assertThrows(ResponseStatusException.class, () -> pc.addPizza(pm));
    }

    @Test
    public void removePizzaTestException() throws Exception {
        doThrow(new PizzaNameNotFoundException("hawaii")).when(ps).removePizza("hawaii");
        assertThrows(ResponseStatusException.class, () -> pc.removePizza("hawaii"));
    }

    @Test
    public void getPizzasTest() {
        when(ps.getAll()).thenReturn(List.of(p1));
        assertEquals(pc.getPizzas(), List.of(p1));
    }

    @Test
    public void getPizzasFilteredTest() {
        when(requestHelper.doRequest(new RequestObject(HttpMethod.GET,8081, "/customers/allergens/"), String[].class)).thenReturn(new String[]{});
        when(ps.getAllByFilter(Set.of())).thenReturn(List.of(p1));
        when(authManager.getNetId()).thenReturn("");
        var res = pc.getPizzasFiltered();
        assertEquals(res, List.of(p1));
        verify(requestHelper, times(1)).doRequest(new RequestObject(HttpMethod.GET,8081, "/customers/allergens/"), String[].class);
        verify(ps, times(1)).getAllByFilter(Set.of());
    }
}
