package nl.tudelft.sem.template.commons.entity;

import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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

    @ElementCollection
    @MapKeyColumn(name = "id")
    private Map<CustomPizza, Integer> pizzasMap;

    public Cart(NetId netId, Map<CustomPizza, Integer> pizzas) {
        this.netId = netId;
        this.pizzasMap = pizzas;
    }

    public void addPizza(CustomPizza pizza) {
        if (pizzasMap.get(pizza) == null) {
            pizzasMap.put(pizza, 1);
            return;
        }
        pizzasMap.put(pizza, pizzasMap.get(pizza) + 1);
    }

    /**
     * @param customPizza
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

    public boolean removePizzaAll(CustomPizza customPizza) {
        if (!pizzasMap.containsKey(customPizza)) {
            return false;
        }
        pizzasMap.remove(customPizza);
        return true;
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
}
