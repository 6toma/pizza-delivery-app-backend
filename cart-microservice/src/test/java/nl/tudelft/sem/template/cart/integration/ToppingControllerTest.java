package nl.tudelft.sem.template.cart.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import nl.tudelft.sem.template.cart.ToppingRepository;
import nl.tudelft.sem.template.cart.ToppingService;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.ToppingModel;
import nl.tudelft.sem.testing.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ToppingControllerTest extends IntegrationTest {

    @Autowired
    private ToppingService toppingService;

    @Autowired
    private ToppingRepository toppingRepository;


    private ToppingModel toppingModel;
    private Topping topping1;
    private Topping topping2;

    @BeforeEach
    void setup() {
        toppingRepository.deleteAll();
        toppingModel = new ToppingModel();
        toppingModel.setName("tomato");
        toppingModel.setPrice(1);
        topping1 = new Topping("tomato", 1);
        topping2 = new Topping("tomato", 8);
    }


    @AfterEach
    void deleteAll() {
        toppingRepository.deleteAll();
        toppingRepository.flush();
    }

    @Test
    void testAddToppingSuccess() throws Exception {
        var result = addToppingRequest(toppingModel);
        result.andExpect(status().isOk());
        assertEquals(1, toppingRepository.count());
        var topping = toppingRepository.findAll().stream().findFirst().get();
        assertEquals(topping, topping1);
    }

    @Test
    void testAddExistingTopping() throws Exception {
        var result = addToppingRequest(toppingModel);
        result.andExpect(status().isOk());
        var result2 = addToppingRequest(toppingModel);
        result2.andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveToppingSuccess() throws Exception {
        var result = addToppingRequest(toppingModel);
        result.andExpect(status().isOk());
        var result2 = removeToppingRequest("tomato");
        result2.andExpect(status().isOk());
        assertEquals(0, toppingRepository.count());
    }

    @Test
    void testRemoveNonExistingTopping() throws Exception {
        var result = addToppingRequest(toppingModel);
        result.andExpect(status().isOk());
        var result2 = removeToppingRequest("notTomato");
        result2.andExpect(status().isBadRequest());
    }

    @Test
    void testEditToppingSuccess() throws Exception {
        var result = addToppingRequest(toppingModel);
        result.andExpect(status().isOk());
        toppingModel.setPrice(8);
        var result2 = editToppingRequest(toppingModel);
        result2.andExpect(status().isOk());
        assertEquals(1, toppingRepository.count());
        var topping = toppingRepository.findAll().stream().findFirst().get();
        assertEquals(topping, topping2);
    }

    @Test
    void testEditNonExistingTopping() throws Exception {
        var result = addToppingRequest(toppingModel);
        result.andExpect(status().isOk());
        toppingModel.setName("notTomato");
        var result2 = editToppingRequest(toppingModel);
        result2.andExpect(status().isBadRequest());
    }

    private ResultActions addToppingRequest(ToppingModel tm) throws Exception {
        return mockMvc.perform(authenticated(post("/topping/add/"), UserRole.REGIONAL_MANAGER)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(tm)));
    }

    private ResultActions removeToppingRequest(String toppingName) throws Exception {
        return mockMvc.perform(authenticated(delete("/topping/remove/"), UserRole.REGIONAL_MANAGER)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toppingName));
    }

    private ResultActions editToppingRequest(ToppingModel tm) throws Exception {
        return mockMvc.perform(authenticated(put("/topping/edit/"), UserRole.REGIONAL_MANAGER)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(tm)));
    }
}
