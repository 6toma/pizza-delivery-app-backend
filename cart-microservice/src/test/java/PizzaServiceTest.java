import nl.tudelft.sem.template.cart.DefaultPizzaRepository;
import nl.tudelft.sem.template.cart.PizzaService;
import nl.tudelft.sem.template.cart.exceptions.PizzaNameAlreadyInUseException;
import nl.tudelft.sem.template.cart.exceptions.PizzaNameNotFoundException;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PizzaServiceTest {

    private final Topping t1 = new Topping("pineapple", 1.5);
    private final DefaultPizza p1 = new DefaultPizza("hawaii", List.of(t1), 6);
    private PizzaService ps;
    private DefaultPizzaRepository pr;

    @BeforeEach
    void beforeEach() {
        pr = Mockito.mock(DefaultPizzaRepository.class);
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
        assertFalse(ps.checkPizzaIsUnique("hawaii"));
    }

    @Test
    public void addPizzaTest() throws Exception {
        when(pr.existsByPizzaName("pineapple")).thenReturn(false);
        Pizza res = ps.addPizza("hawaii", List.of(t1), 6);
        assertEquals(p1, res);
        verify(pr, times(1)).save(p1);
    }

    @Test
    public void addPizzaTestException() {
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
    public void removePizzaTestException() {
        when(pr.existsByPizzaName("hawaii")).thenReturn(false);
        assertThrows(PizzaNameNotFoundException.class, () -> {
            ps.removePizza("hawaii");
        });
    }

    @Test
    public void editPizzaTest() {
        ps.editPizza("hawaii", List.of(t1), 7);
        verify(pr, times(1)).save(new DefaultPizza("hawaii", List.of(t1), 7));
    }
}
