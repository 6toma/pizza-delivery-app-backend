package nl.tudelft.sem.template.cart.integration;

import nl.tudelft.sem.template.cart.DefaultPizzaRepository;
import nl.tudelft.sem.template.cart.PizzaService;
import nl.tudelft.sem.template.cart.ToppingRepository;
import nl.tudelft.sem.template.cart.ToppingService;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.PizzaModel;
import nl.tudelft.sem.template.commons.models.ToppingModel;
import nl.tudelft.sem.testing.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        return mockMvc.perform(authenticated(post("/topping/add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(tm)));
    }

    private ResultActions removeToppingRequest(String toppingName) throws Exception {
        return mockMvc.perform(authenticated(delete("/topping/remove/"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toppingName));
    }

    private ResultActions editToppingRequest(ToppingModel tm) throws Exception {
        return mockMvc.perform(authenticated(put("/topping/edit/"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(tm)));
    }
}
