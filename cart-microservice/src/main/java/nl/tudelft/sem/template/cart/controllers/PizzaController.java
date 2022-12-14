package nl.tudelft.sem.template.cart.controllers;

import java.util.List;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.cart.PizzaService;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.models.PizzaModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pizza")
public class PizzaController {

    private final transient PizzaService pizzaService;

    /**
     * A post request to send a new pizza to the DB.
     *
     * @param pizza the new pizza
     * @return ResponseEntity
     * @throws Exception if the pizza already exists
     */
    @PostMapping("/add")
    public ResponseEntity<String> addPizza(@RequestBody PizzaModel pizza) throws Exception {

        try {
            pizzaService.addPizza(pizza.getPizzaName(), pizza.getToppings(), pizza.getPrice());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok("Pizza added");
    }

    /**
     * A post request to remove a pizza from the DB.
     *
     * @param pizzaName the name of the pizza
     * @return ResponseEntity
     * @throws Exception if the pizza name does not exist
     */
    @DeleteMapping("/remove")
    public ResponseEntity removePizza(@RequestBody String pizzaName) throws Exception {

        try {
            pizzaService.removePizza(pizzaName);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok("Pizza removed");
    }

    /**
     * A put request to edit the toppings of a pizza.
     *
     * @param pizza the new pizza
     * @return ResponseEntity
     * @throws Exception if the pizza name does not exist
     */
    @PutMapping("/edit")
    public ResponseEntity editPizza(@RequestBody PizzaModel pizza) throws Exception {

        try {
            pizzaService.editPizza(pizza.getPizzaName(), pizza.getToppings(), pizza.getPrice());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok("Pizza edited");
    }

    /**
     * Gets all the pizzas from the DB.
     *
     * @return the list of pizzas
     */
    @GetMapping("/getAll")
    public List<Pizza> getPizzas() {
        return pizzaService.getAll();
    }

    /**
     * Gets all the pizzas from the DB filtered on allergens.
     *
     * @param allergens the list of allergens
     * @return the list of filtered pizzas
     */
    @PostMapping("/getAll")
    public List<Pizza> getPizzas(@RequestBody List<String> allergens) {
        return pizzaService.getAllByFilter(allergens);
    }
}
