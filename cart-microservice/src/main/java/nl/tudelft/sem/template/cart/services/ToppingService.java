package nl.tudelft.sem.template.cart.services;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.cart.ToppingRepository;
import nl.tudelft.sem.template.cart.exceptions.ToppingAlreadyInUseException;
import nl.tudelft.sem.template.cart.exceptions.ToppingNotFoundException;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.springframework.stereotype.Service;

/**
 * Menu service responsible for managing the Topping DB.
 */
@Service
@RequiredArgsConstructor
public class ToppingService {

    private final transient ToppingRepository toppingRepository;

    /**
     * Retrieves all the Toppings from the DB.
     *
     * @return List of Toppings
     */
    public List<Topping> getAll() {
        return toppingRepository.findAll();
    }

    /**
     * Adds a Topping to the DB.
     *
     * @param toppingName the name of the topping
     * @param price       the price of the topping
     * @return the resulting Topping
     * @throws Exception when the name of the Topping already exists
     */
    public Topping addTopping(String toppingName, double price) throws Exception {

        if (checkToppingIsUnique(toppingName)) {
            Topping topping = new Topping(toppingName, price);
            toppingRepository.save(topping);

            return topping;
        }
        throw new ToppingAlreadyInUseException(toppingName);
    }

    /**
     * Removes a Topping from the DB.
     *
     * @param toppingName the name of the Topping
     * @throws Exception when no Topping is found with the input name
     */
    public void removeTopping(String toppingName) throws Exception {

        if (!checkToppingIsUnique(toppingName)) {
            Topping t = toppingRepository.findByName(toppingName).get();
            toppingRepository.delete(t);
            return;
        }
        throw new ToppingNotFoundException(toppingName);
    }

    /**
     * Edits a Topping from the DB.
     *
     * @param toppingName the name of the Topping
     * @param price       is the price of the topping
     */
    public void editTopping(String toppingName, double price) throws ToppingNotFoundException {
        var topping = toppingRepository.findByName(toppingName);
        if (topping.isEmpty()) {

            throw new ToppingNotFoundException(toppingName);
        }
        topping.get().setPrice(price);
        toppingRepository.save(topping.get());
    }

    /**
     * Checks whether this Toppingname already exists.
     *
     * @param toppingName the name to check
     * @return true if the name is unique else false
     */
    public boolean checkToppingIsUnique(String toppingName) {
        return !toppingRepository.existsByName(toppingName);
    }

    /**
     * You provide a list of topping names and this method will attempt to find all the toppings by this name. If any of the
     * provided topping names doesn't correspond to a real topping, the {@link ToppingNotFoundException} will be thrown.
     *
     * @param toppingNames The names of the toppings
     * @return The list of actual toppings
     * @throws ToppingNotFoundException Thrown if one of the toppings wasn't found
     */
    public List<Topping> findAllByNames(Collection<String> toppingNames) throws ToppingNotFoundException {
        var toppings = toppingRepository.findAllByNameIn(toppingNames);
        if (toppings.size() != toppingNames.size()) {
            throw new ToppingNotFoundException("");
        }
        return toppings;
    }
}
