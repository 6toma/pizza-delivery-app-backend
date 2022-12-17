package nl.tudelft.sem.store;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nl.tudelft.sem.store.controller.StoreRestController;
import nl.tudelft.sem.store.domain.Store;
import nl.tudelft.sem.store.domain.StoreNotFoundException;
import nl.tudelft.sem.store.domain.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

public class StoreControllerTest {

    private StoreRestController storeRestController;

    private StoreRepository storeRepository;

    @BeforeEach
    void beforeEach() {
        storeRepository = Mockito.mock(StoreRepository.class);
        storeRestController = new StoreRestController(storeRepository);
    }

    @Test
    void test_exception_if_store_id_is_not_in_repository() {
        long storeId = 1;
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notifyStoreOnId(storeId)).isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Store with id " + storeId);
    }

    private void notifyStoreOnId(long storeId) {
        storeRestController.notifyStore(storeId);
    }

    @Test
    void test_prepare_pizza_called() {

        long storeId = 1;
        Store store = Mockito.mock(Store.class);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // notify store on the mocked store
        notifyStoreOnId(storeId);

        // assert that the mocked store prepares pizza
        verify(store).preparePizza();

    }
}
