package nl.tudelft.sem.template.cart;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.cart.exceptions.ToppingAlreadyInUseException;
import nl.tudelft.sem.template.cart.exceptions.ToppingNotFoundException;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Menu service responsible for managing the Topping DB.
 */
@Service
@RequiredArgsConstructor
public class ToppingService {

    private final transient ToppingRepository tr;

    /**
     * Retrieves all the Toppings from the DB.
     *
     * @return List of Toppings
     */
    public List<Topping> getAll() {
        return tr.findAll();
    }

    /**
     * Adds a Topping to the DB.
     *
     * @param toppingName the name of the topping
     * @param price the price of the topping
     * @return the resulting Topping
     * @throws Exception when the name of the Topping already exists
     */
    public Topping addTopping(String toppingName, double price) throws Exception {

        if (checkToppingIsUnique(toppingName)) {
            Topping Topping = new Topping(toppingName, price);
            tr.save(Topping);

            return Topping;
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
            Topping t = tr.findByName(toppingName).get();
            tr.delete(t);
            return;
        }
        throw new ToppingNotFoundException(toppingName);
    }

    /**
     * Edits a Topping from the DB.
     *
     * @param toppingName the name of the Topping
     * @param price is the price of the topping
     */
    public void editTopping(String toppingName, double price) throws ToppingNotFoundException {

        try {
            removeTopping(toppingName);
            addTopping(toppingName, price);
        } catch (Exception e) {
            throw new ToppingNotFoundException(toppingName);
        }

    }

    /**
     * Checks whether this Toppingname already exists.
     *
     * @param toppingName the name to check
     * @return true if the name is unique else false
     */
    public boolean checkToppingIsUnique(String toppingName) {
        return !tr.existsByName(toppingName);
    }

}
