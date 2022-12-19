package nl.tudelft.sem.template.example.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.testing.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

public class ExampleTest extends IntegrationTest {

    @Test
    public void helloWorld() throws Exception {
        ResultActions result = mockMvc.perform(
            authenticated(get("/hello"))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // Assert
        result.andExpect(status().isOk());
        assertResponseEqualsText("Hello " + TEST_USER, result);
    }
}
