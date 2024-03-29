package nl.tudelft.sem.template.cart.mocktest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.cart.ToppingRepository;
import nl.tudelft.sem.template.cart.exceptions.ToppingAlreadyInUseException;
import nl.tudelft.sem.template.cart.exceptions.ToppingNotFoundException;
import nl.tudelft.sem.template.cart.services.ToppingService;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ToppingServiceTest {

    private ToppingRepository tr;

    private ToppingService ts;
    private Topping t1 = new Topping("pineapple", 1.5);


    @BeforeEach
    void beforeEach() {
        tr = Mockito.mock(ToppingRepository.class);
        ts = new ToppingService(tr);
    }

    @Test
    public void getAllTest() {
        when(tr.findAll()).thenReturn(List.of(t1));
        assertEquals(ts.getAll().size(), 1);
    }

    @Test
    public void checkToppingIsUniqueTest() {
        when(tr.existsByName("pineapple")).thenReturn(true);
        assertEquals(false, ts.checkToppingIsUnique("pineapple"));
    }

    @Test
    public void addToppingTest() throws Exception {
        when(tr.existsByName("pineapple")).thenReturn(false);
        Topping res = ts.addTopping("pineapple", 1.5);
        assertEquals(t1, res);
        verify(tr, times(1)).save(t1);
    }

    @Test
    public void addToppingTestException() throws Exception {
        when(tr.existsByName("pineapple")).thenReturn(true);
        assertThrows(ToppingAlreadyInUseException.class, () -> {
            ts.addTopping("pineapple", 1.5);
        });
    }

    @Test
    public void removeToppingTest() throws Exception {
        when(tr.existsByName("pineapple")).thenReturn(true);
        when(tr.findByName("pineapple")).thenReturn(Optional.ofNullable(t1));
        doNothing().when(tr).delete(t1);
        ts.removeTopping("pineapple");
        verify(tr, times(1)).existsByName("pineapple");
        verify(tr, times(1)).findByName("pineapple");
        verify(tr, times(1)).delete(t1);
    }

    @Test
    public void removeToppingTestException() throws Exception {
        when(tr.existsByName("pineapple")).thenReturn(false);
        assertThrows(ToppingNotFoundException.class, () -> {
            ts.removeTopping("pineapple");
        });
    }

    @Test
    public void editToppingEmptyTest() throws Exception {
        when(tr.findByName("pineapple")).thenReturn(Optional.empty());
        assertThrows(ToppingNotFoundException.class, () -> {
            ts.removeTopping("pineapple");
        });
    }
}
