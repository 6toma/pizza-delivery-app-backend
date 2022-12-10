package nl.tudelft.sem.store;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.store.domain.Store;
import nl.tudelft.sem.store.domain.StoreNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StoreControllerIntegrationTest {
    @Autowired
    private MockMvc mockmvc;

    /**
     * Converts object to Json in order simulate a <i>real</i> post request.
     *
     * @param obj object to convert to json
     * @return a json representation of the object
     */
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_database_loaded_and_ids_work() throws Exception {
        for (Store store : StoreDataLoader.DEFAULT_STORE_LIST) {
            // assert that the store notification returns HTTP OK status code (200)
            notifyStoreHelper(store.getStoreId())
                .andExpect(status().isOk());
        }
    }

    private ResultActions notifyStoreHelper(long storeId) throws Exception {
        NotifyStoreRequest notifyStoreRequest = new NotifyStoreRequest(storeId);

        return mockmvc.perform(post("/store/notify")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(notifyStoreRequest)));
    }

    @Test
    public void test_wrong_store_id_results_in_exception() {
        // perform request with a store_id that we do no have
        assertThatThrownBy(() -> notifyStoreHelper(-1))
            .rootCause()
            .isInstanceOf(StoreNotFoundException.class);
    }
}
