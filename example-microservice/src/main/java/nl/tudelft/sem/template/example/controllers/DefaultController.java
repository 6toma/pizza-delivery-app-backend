package nl.tudelft.sem.template.example.controllers;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.AuthManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
@RequiredArgsConstructor
public class DefaultController {

    private final transient AuthManager authManager;

    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getEmail());

    }

}
