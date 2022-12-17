import nl.tudelft.sem.template.cart.PizzaRepository;
import nl.tudelft.sem.template.cart.PizzaService;
import nl.tudelft.sem.template.cart.ToppingRepository;
import nl.tudelft.sem.template.cart.ToppingService;
import nl.tudelft.sem.template.cart.controllers.PizzaController;
import nl.tudelft.sem.template.cart.exceptions.PizzaNameAlreadyInUseException;
import nl.tudelft.sem.template.cart.exceptions.PizzaNameNotFoundException;
import nl.tudelft.sem.template.cart.exceptions.ToppingAlreadyInUseException;
import nl.tudelft.sem.template.cart.exceptions.ToppingNotFoundException;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.PizzaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PizzaServiceTest {

    private Topping t1 = new Topping("pineapple", 1.5);
    private Pizza p1 = new Pizza("hawaii", List.of(t1), 6);
    private PizzaService ps;
    private PizzaRepository pr;

    @BeforeEach
    void beforeEach() {
        pr = Mockito.mock(PizzaRepository.class);
        ps = new PizzaService(pr);
    }

    @Test
    public void getAllTest() {
        when(pr.findAll()).thenReturn(List.of(p1));
        assertEquals(ps.getAll().size(), 1);
    }

    @Test
    public void checkPizzaIsUniqueTest() {
        when(pr.existsByPizzaName("hawaii")).thenReturn(true);
        assertEquals(false, ps.checkPizzaIsUnique("hawaii"));
    }

    @Test
    public void addPizzaTest() throws Exception {
        when(pr.existsByPizzaName("pineapple")).thenReturn(false);
        Pizza res = ps.addPizza("hawaii", List.of(t1), 6);
        assertEquals(p1, res);
        verify(pr, times(1)).save(p1);
    }

    @Test
    public void addPizzaTestException() throws Exception {
        when(pr.existsByPizzaName("hawaii")).thenReturn(true);
        assertThrows(PizzaNameAlreadyInUseException.class, () -> {
            ps.addPizza("hawaii", List.of(t1), 6);
        });
    }

    @Test
    public void removePizzaTest() throws Exception {
        when(pr.existsByPizzaName("hawaii")).thenReturn(true);
        ps.removePizza("hawaii");
        verify(pr, times(1)).deleteByPizzaName("hawaii");
    }

    @Test
    public void removePizzaTestException() throws Exception {
        when(pr.existsByPizzaName("hawaii")).thenReturn(false);
        assertThrows(PizzaNameNotFoundException.class, () -> {
            ps.removePizza("hawaii");
        });
    }

    @Test
    public void editPizzaTest() throws Exception {
        ps.editPizza("hawaii", List.of(t1), 7);
        verify(pr, times(1)).save(new Pizza("hawaii", List.of(t1), 7));
    }
}
