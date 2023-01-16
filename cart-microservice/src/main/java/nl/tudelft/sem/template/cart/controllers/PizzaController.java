package nl.tudelft.sem.template.cart.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.annotations.role.RoleRegionalManager;
import nl.tudelft.sem.template.cart.exceptions.ToppingNotFoundException;
import nl.tudelft.sem.template.cart.services.PizzaService;
import nl.tudelft.sem.template.cart.services.ToppingService;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.models.PizzaModel;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import nl.tudelft.sem.template.commons.utils.RequestObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    private final transient ToppingService toppingService;
    private final transient RequestHelper requestHelper;
    private final transient AuthManager authManager;

    /**
     * A post request to send a new pizza to the DB.
     *
     * @param pizza the new pizza
     * @return ResponseEntity
     * @throws ToppingNotFoundException Thrown if one of the provided topping names wasn't found in the database
     */
    @PostMapping("/add")
    @RoleRegionalManager
    public ResponseEntity<String> addPizza(@Validated @RequestBody PizzaModel pizza) throws ToppingNotFoundException {
        var toppings = toppingService.findAllByNames(pizza.getToppings());
        try {
            var newPizza = pizzaService.addPizza(pizza.getPizzaName(), toppings, pizza.getPrice());
            return ResponseEntity.ok("Pizza added with id " + newPizza.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

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
    public ResponseEntity<String> removePizza(@RequestBody String pizzaName) throws Exception {

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
     */
    @PutMapping("/edit")
    @RoleRegionalManager
    public ResponseEntity<String> editPizza(@RequestBody PizzaModel pizza) throws ToppingNotFoundException {
        var toppings = toppingService.findAllByNames(pizza.getToppings());
        try {
            pizzaService.editPizza(pizza.getPizzaName(), toppings, pizza.getPrice());
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
    public List<DefaultPizza> getPizzas() {
        return pizzaService.getAll();
    }

    /**
     * Gets all the pizzas from the DB filtered on allergens.
     *
     * @return the list of filtered pizzas
     */
    @GetMapping("/getAllFiltered")
    public List<DefaultPizza> getPizzasFiltered() {
        var allergens = getUserAllergens(authManager.getNetId());
        return pizzaService.getAllByFilter(allergens);
    }

    private Set<String> getUserAllergens(String netId) {
        return Arrays.stream(requestHelper.doRequest(new RequestObject(HttpMethod.GET,8081, "/customers/allergens/" + netId), String[].class))
            .collect(Collectors.toSet());
    }
}
