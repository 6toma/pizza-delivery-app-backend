package nl.tudelft.sem.template.cart.integration;

import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.cart.DefaultPizzaRepository;
import nl.tudelft.sem.template.cart.PizzaService;
import nl.tudelft.sem.template.cart.ToppingRepository;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.PizzaModel;
import nl.tudelft.sem.template.commons.models.PizzaToppingModel;
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

public class PizzaControllerTest extends IntegrationTest {

    @Autowired
    private PizzaService pizzaService;

    @Autowired
    private ToppingRepository toppingRepository;

    @Autowired
    private DefaultPizzaRepository defaultRepository;

    private PizzaModel pizzaModel;
    private Pizza pizza1;
    private Pizza pizza2;

    @BeforeEach
    void setup() {
        defaultRepository.deleteAll();
        var toppings1 = toppingRepository.saveAll(Arrays.asList(new Topping("test1", 10),
                new Topping("test2", 13),
                new Topping("test3", 15)));
        pizzaModel = new PizzaModel();
        pizzaModel.setPizzaName("Hawaii");
        pizzaModel.setPrice(7);
        pizzaModel.setToppings(toppings1);
        pizza1 = new DefaultPizza("Hawaii", toppings1, 7);
        pizza2 = new DefaultPizza("Hawaii", toppings1, 8);
    }

    @Test
    void testAddPizzaSuccess() throws Exception {
        var result = addPizzaRequest(pizzaModel);
        result.andExpect(status().isOk());
        assertEquals(1, defaultRepository.count());
        var pizza = defaultRepository.findAll().stream().findFirst().get();
        assertEquals(pizza, pizza1);
    }

    @Test
    void testAddExistingPizza() throws Exception {
        var result = addPizzaRequest(pizzaModel);
        result.andExpect(status().isOk());
        var result2 = addPizzaRequest(pizzaModel);
        result2.andExpect(status().isBadRequest());
    }

    @Test
    void testRemovePizzaSuccess() throws Exception {
        var result = addPizzaRequest(pizzaModel);
        result.andExpect(status().isOk());
        var result2 = removePizzaRequest("Hawaii");
        result2.andExpect(status().isOk());
        assertEquals(0, defaultRepository.count());
    }

    @Test
    void testRemoveNonExistingPizza() throws Exception {
        var result = addPizzaRequest(pizzaModel);
        result.andExpect(status().isOk());
        var result2 = removePizzaRequest("NotHawaii");
        result2.andExpect(status().isBadRequest());
    }

    @Test
    void testEditPizzaSuccess() throws Exception {
        var result = addPizzaRequest(pizzaModel);
        result.andExpect(status().isOk());
        pizzaModel.setPrice(8);
        var result2 = editPizzaRequest(pizzaModel);
        result2.andExpect(status().isOk());
        var pizza = defaultRepository.findAll().stream().findFirst().get();

        assertEquals(pizza, pizza2);
    }

    @Test
    void testEditNonExistingPizza() throws Exception {
        var result = addPizzaRequest(pizzaModel);
        result.andExpect(status().isOk());
        pizzaModel.setPizzaName("NotHawaii");
        var result2 = editPizzaRequest(pizzaModel);
        result2.andExpect(status().isBadRequest());
    }

    private ResultActions addPizzaRequest(PizzaModel pm) throws Exception {
        return mockMvc.perform(authenticated(post("/pizza/add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(pm)));
    }

    private ResultActions removePizzaRequest(String pizzaName) throws Exception {
        return mockMvc.perform(authenticated(delete("/pizza/remove/"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(pizzaName));
    }

    private ResultActions editPizzaRequest(PizzaModel pm) throws Exception {
        return mockMvc.perform(authenticated(put("/pizza/edit/"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(pm)));
    }
}
