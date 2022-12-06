package nl.tudelft.sem.template.example;

import java.util.List;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.example.exceptions.PizzaNameAlreadyInUseException;
import nl.tudelft.sem.template.example.exceptions.PizzaNameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Menu service responsible for managing the pizza DB.
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final transient PizzaRepository pizzaRepository;

    /**
     * Retrieves all the pizzas from the DB.
     *
     * @return List of pizzas
     */
    public List<Pizza> getAll() {
        return pizzaRepository.findAll();
    }

    /**
     * Retrieves all the pizzas from the DB that do not contain any of the allergens.
     *
     * @param allergens the list of allergens
     * @return List of filtered pizzas
     */
    public List<Pizza> getAllByFilter(List<String> allergens) {
        List<Pizza> pizzas = pizzaRepository.findAll();
        //Todo implement filter
        return pizzas;
    }

    /**
     * Adds a pizza to the DB.
     *
     * @param pizzaName the name of the pizza
     * @param toppings the toppings on the pizza
     * @return the resulting Pizza
     * @throws Exception when the name of the pizza already exists
     */
    public Pizza addPizza(String pizzaName, List<Topping> toppings) throws Exception {

        if (checkPizzaIsUnique(pizzaName)) {
            Pizza pizza = new Pizza(pizzaName, toppings);
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
            pizzaRepository.deleteById(pizzaName);
            return;
        }
        throw new PizzaNameNotFoundException(pizzaName);
    }

    /**
     * Edits a piza from the DB.
     *
     * @param pizzaName the name of the pizza
     * @param toppings the topppings on the pizza
     * @throws Exception when no pizza is found with the input name
     */
    public void editPizza(String pizzaName, List<Topping> toppings) throws Exception {

        try {
            removePizza(pizzaName);
            addPizza(pizzaName, toppings);
        } catch (Exception e) {
            throw new PizzaNameNotFoundException(pizzaName);
        }
    }

    /**
     * Checks whether this pizzaname already exists.
     *
     * @param pizzaName the name to check
     * @return true if the name is unique else false
     */
    public boolean checkPizzaIsUnique(String pizzaName) {
        return !pizzaRepository.existsByPizzaName(pizzaName);
    }

}
