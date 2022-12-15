package nl.tudelft.sem.template.cart.controllers;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.annotations.role.RoleRegionalManager;
import nl.tudelft.sem.template.cart.PizzaService;
import nl.tudelft.sem.template.commons.models.PizzaModel;
import nl.tudelft.sem.template.commons.entity.Pizza;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
    @RoleRegionalManager
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
    @RoleRegionalManager
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
    @RoleRegionalManager
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
