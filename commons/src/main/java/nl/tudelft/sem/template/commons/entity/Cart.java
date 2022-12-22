package nl.tudelft.sem.template.commons.entity;

import java.util.List;
import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.authentication.NetId;

@Entity
@Table(name = "carts")
@NoArgsConstructor
@Getter
public class Cart {
    @EmbeddedId
    private NetId netId;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "id")
    private Map<CustomPizza, Integer> pizzasMap;

    public Cart(NetId netId, Map<CustomPizza, Integer> pizzas) {
        this.netId = netId;
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

    public boolean addTopping(CustomPizza pizza, Topping topping) {
        if (pizzasMap.get(pizza) == null) {
            return false;
        }
        if (pizzasMap.get(pizza) == 1) {
            pizzasMap.remove(pizza);
        } else {
            pizzasMap.put(pizza, pizzasMap.get(pizza) - 1);
        }
        pizza.addTopping(topping);
        pizzasMap.put(pizza, pizzasMap.get(pizza) + 1);
        return true;
    }

    public boolean removeTopping(CustomPizza pizza, Topping topping) {
        if (pizzasMap.get(pizza) == null) {
            return false;
        }
        if (pizzasMap.get(pizza) == 1) {
            pizzasMap.remove(pizza);
        } else {
            pizzasMap.put(pizza, pizzasMap.get(pizza) - 1);
        }
        pizza.removeTopping(topping);
        pizzasMap.put(pizza, pizzasMap.get(pizza) + 1);
        return true;
    }

    @ManyToMany
    private List<Pizza> pizzas;

    public Cart(NetId netId, List<Pizza> pizzas) {
        this.netId = netId;
        this.pizzas = pizzas;
    }

    public void addPizza(Pizza pizza) {
        pizzas.add(pizza);

    }
}
