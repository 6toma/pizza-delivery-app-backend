package nl.tudelft.sem.template.cart.controllers;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.cart.PizzaService;
import nl.tudelft.sem.template.cart.ToppingService;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.PizzaModel;
import nl.tudelft.sem.template.commons.models.ToppingModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/topping")
public class ToppingController {

    private final transient ToppingService ts;

    /**
     * A post request to send a new topping to the DB.
     *
     * @param tm the new topping
     * @return ResponseEntity
     * @throws Exception if the topping already exists
     */
    @PostMapping("/add")
    public ResponseEntity<String> addTopping(@RequestBody ToppingModel tm) throws Exception {

        try {
            ts.addTopping(tm.getName(), tm.getPrice());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok("Topping added");
    }

    /**
     * A post request to remove a topping from the DB.
     *
     * @param name the name of the topping
     * @return ResponseEntity
     * @throws Exception if the topping name does not exist
     */
    @DeleteMapping("/remove")
    public ResponseEntity removeTopping(@RequestBody String name) throws Exception {

        try {
            ts.removeTopping(name);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok("Topping removed");
    }

    /**
     * A put request to edit the toppings of a pizza
     *
     * @param tm the new topping
     * @return ResponseEntity
     * @throws Exception if the topping name does not exist
     */
    @PutMapping("/edit")
    public ResponseEntity editTopping(@RequestBody ToppingModel tm) throws Exception {

        try {
            ts.editTopping(tm.getName(), tm.getPrice());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok("Topping edited");
    }

    /**
     * Gets all the toppings from the DB.
     *
     * @return the list of toppings
     */
    @GetMapping("/getAll")
    public List<Topping> getToppings() {
        return ts.getAll();
    }

}
