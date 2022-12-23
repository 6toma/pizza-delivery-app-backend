package nl.tudelft.sem.template.store;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import javax.mail.MessagingException;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.store.controller.StoreRestController;
import nl.tudelft.sem.template.store.domain.Store;
import nl.tudelft.sem.template.store.domain.StoreRepository;
import nl.tudelft.sem.template.store.services.EmailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

public class StoreControllerTest {

    private StoreRestController storeRestController;

    private StoreRepository storeRepository;
    private EmailNotificationService emailNotificationService;

    @BeforeEach
    void beforeEach() {
        storeRepository = Mockito.mock(StoreRepository.class);
        emailNotificationService = Mockito.mock(EmailNotificationService.class);
        storeRestController = new StoreRestController(storeRepository, emailNotificationService);
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
    void test_prepare_pizza_called() throws MessagingException, IOException {

        long storeId = 1;
        Store store = Mockito.mock(Store.class);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(store.getStoreOwnerNetId()).thenReturn(new NetId("test@user.com"));

        // notify store on the mocked store
        notifyStoreOnId(storeId);

        // assert that the mocked store prepares pizza
        verify(emailNotificationService, times(1)).notifyOrder(any());
    }

    @Test
    public void test_notify_store_bad_email() throws MessagingException, IOException {

        long storeId = 1;
        Store store = Mockito.mock(Store.class);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(store.getStoreOwnerNetId()).thenReturn(new NetId("test@user.com"));
        doThrow(MessagingException.class).when(emailNotificationService).notifyOrder("test@user.com");

        assertThatThrownBy(() -> storeRestController.notifyStore(storeId)).isInstanceOf(ResponseStatusException.class);

        // assert that the mocked store prepares pizza
        verify(emailNotificationService, times(1)).notifyOrder(any());
    }


    @Test
    public void test_notify_store_remove_order_bad_email() throws MessagingException, IOException {

        long storeId = 1;
        Store store = Mockito.mock(Store.class);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(store.getStoreOwnerNetId()).thenReturn(new NetId("test@user.com"));
        doThrow(MessagingException.class).when(emailNotificationService).notifyOrderRemove("test@user.com");

        assertThatThrownBy(() -> storeRestController.notifyRemoveOrder(storeId)).isInstanceOf(ResponseStatusException.class);

        // assert that the mocked store prepares pizza
        verify(emailNotificationService, times(1)).notifyOrderRemove(any());
    }

    @Test
    public void test_notify_store_remove_order_good_email() throws MessagingException, IOException {

        long storeId = 1;
        Store store = Mockito.mock(Store.class);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(store.getStoreOwnerNetId()).thenReturn(new NetId("test@user.com"));

        storeRestController.notifyRemoveOrder(storeId);

        // assert that the mocked store prepares pizza
        verify(emailNotificationService, times(1)).notifyOrderRemove(any());
    }

    @Test
    public void test_notify_store_remove_order_bad_id() throws MessagingException, IOException {

        long storeId = 1;
        Store store = Mockito.mock(Store.class);
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storeRestController.notifyRemoveOrder(storeId))
            .isInstanceOf(ResponseStatusException.class);

        // assert that the mocked store prepares pizza
        verify(emailNotificationService, times(0)).notifyOrderRemove(any());
    }
}
