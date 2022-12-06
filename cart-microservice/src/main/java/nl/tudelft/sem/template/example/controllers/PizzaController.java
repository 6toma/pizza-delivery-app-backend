package nl.tudelft.sem.template.example.controllers;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.example.MenuService;
import nl.tudelft.sem.template.example.Pizza;
import nl.tudelft.sem.template.example.models.PizzaModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PizzaController {

    private final transient MenuService menuService;

    /**
     * A post request to send a new pizza to the DB.
     *
     * @param pizza the new pizza
     * @return ResponseEntity
     * @throws Exception if the pizza already exists
     */
    @PostMapping("/pizza/addPizza")
    public ResponseEntity<String> addPizza(@RequestBody PizzaModel pizza) throws Exception {

        try {
            menuService.addPizza(pizza.getPizzaName(), pizza.getToppings());
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
    @PostMapping("/pizza/removePizza")
    public ResponseEntity removePizza(@RequestBody String pizzaName) throws Exception {

        try {
            menuService.removePizza(pizzaName);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok("Pizza removed");
    }

    /**
     * A put request to edit the toppings of a pizza
     *
     * @param pizza the new pizza
     * @return ResponseEntity
     * @throws Exception if the pizza name does not exist
     */
    @PutMapping("/pizza/editPizza")
    public ResponseEntity editPizza(@RequestBody PizzaModel pizza) throws Exception {

        try {
            menuService.editPizza(pizza.getPizzaName(), pizza.getToppings());
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
    @GetMapping("/pizzas")
    public List<Pizza> getPizzas() {
        return menuService.getAll();
    }

    /**
     * Gets all the pizzas from the DB filtered on allergens.
     *
     * @param allergens the list of allergens
     * @return the list of filtered pizzas
     */
    @PostMapping("/pizzas")
    public List<Pizza> getPizzas(@RequestBody List<String> allergens) {
        return menuService.getAllByFilter(allergens);
    }
}
