package nl.tudelft.sem.template.commons.entity;

import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.authentication.UserEmail;

@Entity
@Table(name = "carts")
@NoArgsConstructor
@Getter
public class Cart {
    @EmbeddedId
    private UserEmail userEmail;

    @ElementCollection
    @MapKeyColumn(name = "id")
    private Map<CustomPizza, Integer> pizzasMap;

    public Cart(UserEmail userEmail, Map<CustomPizza, Integer> pizzas) {
        this.userEmail = userEmail;
        this.pizzasMap = pizzas;
    }

    /**
     * Method that adds a new pizza to the cart by incrementing the amount that was previously there.
     *
     * @param pizza custom pizza to add to the cart
     */
    public void addPizza(CustomPizza pizza) {
        if (pizzasMap.get(pizza) == null) {
            pizzasMap.put(pizza, 1);
            return;
        }
        pizzasMap.put(pizza, pizzasMap.get(pizza) + 1);
    }

    /**
     * Removes a pizza from a cart by decrementing the amount.
     *
     * @param customPizza custom pizza that is in cart
     * @return Returns whether the pizza still exists in the cart after decrementing
     */
    public boolean removePizza(CustomPizza customPizza) {
        if (pizzasMap.get(customPizza) == null) {
            return false;
        }
        if (pizzasMap.get(customPizza) == 1) {
            pizzasMap.remove(customPizza);
            return false;
        } else {
            pizzasMap.put(customPizza, pizzasMap.get(customPizza) - 1);
        }
        return true;
    }

    /**
     * Remove a custom pizza entirely from the cart.
     *
     * @param customPizza custom pizza to remove entirely
     */
    public void removePizzaAll(CustomPizza customPizza) {
        if (!pizzasMap.containsKey(customPizza)) {
            return;
        }
        pizzasMap.remove(customPizza);
    }
}
