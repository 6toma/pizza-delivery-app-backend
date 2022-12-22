package nl.tudelft.sem.template.cart.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.testing.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

public class PizzaControllerTest extends IntegrationTest {

    @Test
    public void helloWorld() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.

        Pizza pizza = new DefaultPizza("Test", Arrays.asList(new Topping("dough", 0.5)), 0.5);
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ResultActions result = mockMvc.perform(authenticated(get("/pizza/getAll"))
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer MockedToken"));


        // Assert
        result.andExpect(status().isOk());
        String response = result.andReturn().getResponse().getContentAsString();
        //        assertThat(response).isEqualTo("");

    }
}
