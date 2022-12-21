package nl.tudelft.sem.template.cart;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.cart.exceptions.PizzaNameAlreadyInUseException;
import nl.tudelft.sem.template.cart.exceptions.PizzaNameNotFoundException;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public List<DefaultPizza> getAllByFilter(List<String> allergens) {
        List<DefaultPizza> pizzas = pizzaRepository.findAll();
        //Todo implement filter
        return pizzas;
    }

    /**
     * Adds a pizza to the DB.
     *
     * @param pizzaName the name of the pizza
     * @param toppings  the toppings on the pizza
     * @return the resulting Pizza
     * @throws Exception when the name of the pizza already exists
     */
    public DefaultPizza addPizza(String pizzaName, List<Topping> toppings, double price) throws Exception {

        if (checkPizzaIsUnique(pizzaName)) {
            DefaultPizza pizza = new DefaultPizza(pizzaName, toppings, price);
            pizzaRepository.save(pizza);

            return pizza;
        }
        throw new PizzaNameAlreadyInUseException(pizzaName);
    }

    /**
     * Removes a pizza from the DB.
     *
     * @param pizzaName the name of the pizza
     * @throws Exception when no pizza is found with the input name
     */
    public void removePizza(String pizzaName) throws Exception {

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
     */
    public void editPizza(String pizzaName, List<Topping> toppings, double price) throws PizzaNameNotFoundException {
        try {
            removePizza(pizzaName);
            addPizza(pizzaName, toppings, price);
        } catch (Exception e) {
            throw new PizzaNameNotFoundException(pizzaName);
        }

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
