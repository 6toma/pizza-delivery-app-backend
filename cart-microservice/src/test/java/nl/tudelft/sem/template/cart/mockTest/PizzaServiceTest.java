package nl.tudelft.sem.template.cart.mockTest;

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
import java.util.Optional;

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
        when(pr.findByPizzaName("hawaii")).thenReturn(Optional.of(p1));
        when(pr.existsByPizzaName("hawaii")).thenReturn(true);
        doNothing().when(pr).delete(p1);
        ps.removePizza("hawaii");
        verify(pr, times(1)).delete(p1);
        verify(pr, times(1)).findByPizzaName("hawaii");
    }

    @Test
    public void removePizzaTestException() {
        when(pr.findByPizzaName("hawaii")).thenReturn(Optional.of(p1));
        when(pr.existsByPizzaName("hawaii")).thenReturn(false);
        assertThrows(PizzaNameNotFoundException.class, () -> {
            ps.removePizza("hawaii");
        });
    }
}