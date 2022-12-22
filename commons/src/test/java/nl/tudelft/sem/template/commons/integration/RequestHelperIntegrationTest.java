package nl.tudelft.sem.template.commons.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.tudelft.sem.template.authentication.annotations.role.MicroServiceInteraction;
import nl.tudelft.sem.template.authentication.annotations.role.RoleCustomer;
import nl.tudelft.sem.template.authentication.config.RequestAuthenticationConfig;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest(
    classes = {ServletWebServerFactoryAutoConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "jwt.secret=somesecret"
    }
)
@ComponentScan({"nl.tudelft.sem.template.commons.utils", "nl.tudelft.sem.template.authentication",
    "nl.tudelft.sem.template.commons.integration"})
@Import(RequestAuthenticationConfig.class)
@EnableAutoConfiguration
class RequestHelperIntegrationTest {

    @RestController
    static class TestController {
        @GetMapping("/unauthorized")
        @RoleCustomer
        String testUnauthorized() {
            return "/unauthorized";
        }

        @GetMapping("/get")
        @MicroServiceInteraction
        String testMicroserviceInteractionGet() {
            return "/get";
        }

        @PostMapping("/post")
        @MicroServiceInteraction
        String testMicroserviceInteractionPost() {
            return "/post";
        }

        @DeleteMapping("/delete")
        @MicroServiceInteraction
        String testMicroserviceInteractionDelete() {
            return "/delete";
        }
    }

    @LocalServerPort
    private int port;

    @Autowired
    private RequestHelper requestHelper;

    @Test
    void testHelperGet() {
        var result = requestHelper.getRequest(port, "/get", String.class);
        assertThat(result).isEqualTo("/get");
    }

    @Test
    void testHelperPost() {
        var result = requestHelper.postRequest(port, "/post", "", String.class);
        assertThat(result).isEqualTo("/post");
    }

    @Test
    void testHelperDelete() {
        var result = requestHelper.deleteRequest(port, "/delete", String.class);
        assertThat(result).isEqualTo("/delete");
    }

    @Test
    void testHelperUnauthorized() {
        assertThrows(HttpClientErrorException.Forbidden.class,
            () -> requestHelper.getRequest(port, "/unauthorized", String.class));
    }


}