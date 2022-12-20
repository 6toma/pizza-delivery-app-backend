package nl.tudelft.sem.store;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import nl.tudelft.sem.store.domain.Store;
import nl.tudelft.sem.store.domain.StoreOwnerValidModel;
import nl.tudelft.sem.testing.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class StoreControllerIntegrationTest extends IntegrationTest {

    final List<Store> defaultStoreList = StoreDataLoader.DEFAULT_STORE_LIST;

    @Test
    public void test_get_all() throws Exception {
        var storeNames = defaultStoreList.stream().map(Store::getStoreName).collect(Collectors.toList());
        String[] storeNameArray = storeNames.toArray(new String[0]);
        doRequest(get("/store/getStoreNames")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("@", hasSize(storeNames.size())))
            .andExpect(jsonPath("@", containsInAnyOrder(storeNameArray)));
    }

    @Test
    public void test_store_name_exists() throws Exception {
        // tests if all the items from the store
        for (Store store : defaultStoreList) {
            doRequest(post("/store/getStoreByName").content(store.getStoreName()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        }
    }

    @Test
    public void test_store_name_not_exists() throws Exception {
        // if not existing stores return false
        for (String notExistingStore : List.of("Not existing", "Not a store", "Not a store name")) {
            doRequest(post("/store/getStoreByName").content(notExistingStore))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
        }
    }

    @SneakyThrows
    private void expectStoreOwnerToBe(StoreOwnerValidModel storeOwnerValidModel, boolean expect) {
        doRequest(post("/store/checkStoreowner").contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(storeOwnerValidModel)))
            .andExpect(status().isOk())
            .andExpect(content().string(String.valueOf(expect)));
    }

    @Test
    public void test_owner_id_fake_but_good_store() {
        // store owner has netId of store index 0
        // the store_id that is passed is actually store index 1
        Store store = defaultStoreList.get(0);
        StoreOwnerValidModel storeOwnerValidModel =
            new StoreOwnerValidModel(
                store.getStoreOwnerNetId().toString(), defaultStoreList.get(1).getStoreId());
        expectStoreOwnerToBe(storeOwnerValidModel, false);
    }


    @Test
    public void test_all_valid_store_owners() {
        for (Store store : defaultStoreList) {
            StoreOwnerValidModel storeOwnerValidModel =
                new StoreOwnerValidModel(
                    store.getStoreOwnerNetId().toString(), store.getStoreId());
            expectStoreOwnerToBe(storeOwnerValidModel, true);
        }
    }

    @Test
    public void test_check_store_owner_but_not_existing_store_id() throws Exception {
        // store number -1 is invalid
        StoreOwnerValidModel storeOwnerValidModel =
            new StoreOwnerValidModel(defaultStoreList.get(0).getStoreOwnerNetId().toString(), -1);

        doRequest(post("/store/checkStoreowner").contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(storeOwnerValidModel)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void test_get_store_id_from_valid_store_name() {
        for (Store store : defaultStoreList) {
            doRequest(post("/store/getStoreIdFromName")
                .content(store.getStoreName()))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(store.getStoreId())));
        }
    }

    @Test
    @SneakyThrows
    public void test_get_store_id_from_invalid_store_name_should_return_minus_1() {
        doRequest(post("/store/getStoreIdFromName")
            .content("Invalid store name"))
            .andExpect(status().isOk())
            .andExpect(content().string(String.valueOf(-1)));
    }

    @Test
    public void test_notify_existing_stores_works() throws Exception {
        for (Store store : defaultStoreList) {
            // assert that the store notification returns HTTP OK status code (200)
            doRequest(post("/store/notify").contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(store.getStoreId())))
                .andExpect(status().isOk());
        }
    }

    @Test
    public void test_notify_not_existing_store_gives_error() throws Exception {
        doRequest(post("/store/notify").contentType(MediaType.APPLICATION_JSON)
            .content(String.valueOf(-1)))
            .andExpect(status().isBadRequest());
    }
}
