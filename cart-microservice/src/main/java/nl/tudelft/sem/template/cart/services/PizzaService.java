package nl.tudelft.sem.template.cart.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.cart.DefaultPizzaRepository;
import nl.tudelft.sem.template.cart.exceptions.PizzaNameAlreadyInUseException;
import nl.tudelft.sem.template.cart.exceptions.PizzaNameNotFoundException;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.springframework.stereotype.Service;

/**
 * Menu service responsible for managing the pizza DB.
 */
@Service
@RequiredArgsConstructor
public class PizzaService {

    private final transient DefaultPizzaRepository pizzaRepository;

    /**
     * Retrieves all the pizzas from the DB.
     *
     * @return List of pizzas
     */
    public List<DefaultPizza> getAll() {
        return pizzaRepository.findAll();
    }

    /**
     * Retrieves all the pizzas from the DB that do not contain any of the allergens.
     *
     * @param allergens the list of allergens
     * @return List of filtered pizzas
     */
    public List<DefaultPizza> getAllByFilter(Set<String> allergens) {
        return pizzaRepository.findAll().stream()
            .filter(pizza -> pizza.getToppings().stream().noneMatch(topping -> allergens.contains(topping.getName())))
            .collect(Collectors.toList());
    }

    /**
     * Adds a pizza to the DB.
     *
     * @param pizzaName the name of the pizza
     * @param toppings  the toppings on the pizza
     * @return the resulting Pizza
     * @throws PizzaNameAlreadyInUseException when the name of the pizza already exists
     */
    public DefaultPizza addPizza(String pizzaName, List<Topping> toppings, double price)
        throws PizzaNameAlreadyInUseException {
        if (checkPizzaIsUnique(pizzaName)) {
            DefaultPizza pizza = new DefaultPizza(pizzaName, toppings, price);
            return pizzaRepository.save(pizza);
        }
        throw new PizzaNameAlreadyInUseException(pizzaName);
    }

    /**
     * Removes a pizza from the DB.
     *
     * @param pizzaName the name of the pizza
     * @throws PizzaNameNotFoundException when no pizza is found with the input name
     */
    public void removePizza(String pizzaName) throws PizzaNameNotFoundException {

        if (!checkPizzaIsUnique(pizzaName)) {
            DefaultPizza p = pizzaRepository.findByPizzaName(pizzaName).get();
            pizzaRepository.delete(p);
            return;
        }
        throw new PizzaNameNotFoundException(pizzaName);
    }

    /**
     * Edits a pizza from the DB.
     *
     * @param pizzaName the name of the pizza
     * @param toppings  the topppings on the pizza
     * @throws PizzaNameNotFoundException Thrown when pizza not found by name
     */
    public void editPizza(String pizzaName, List<Topping> toppings, double price) throws PizzaNameNotFoundException {
        var defaultPizza = pizzaRepository.findByPizzaName(pizzaName);
        if (defaultPizza.isEmpty()) {
            throw new PizzaNameNotFoundException(pizzaName);
        }
        defaultPizza.get().setPrice(price);
        defaultPizza.get().setToppings(toppings);
    }

    /**
     * Checks whether this pizzaName already exists.
     *
     * @param pizzaName the name to check
     * @return true if the name does not yet exist, else false
     */
    public boolean checkPizzaIsUnique(String pizzaName) {
        return !pizzaRepository.existsByPizzaName(pizzaName);
    }

    public Optional<DefaultPizza> getPizza(String pizzaName) {
        return pizzaRepository.findByPizzaName(pizzaName);
    }

}
