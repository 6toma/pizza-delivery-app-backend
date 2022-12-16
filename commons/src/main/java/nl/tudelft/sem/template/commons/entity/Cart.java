package nl.tudelft.sem.template.commons.entity;

import java.util.List;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
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
    @ManyToMany
    private List<Pizza> pizzas;

    public Cart(NetId netId, List<Pizza> pizzas) {
        this.netId = netId;
        this.pizzas = pizzas;
    }

    public void addPizza(Pizza pizza) {
        pizzas.add(pizza);
    }

    public void removePizza(Pizza pizza) {
        pizzas.remove(pizza);
    }

    public boolean addTopping(Pizza pizza, Topping topping) {
        int index = pizzas.indexOf(pizza);
        boolean success = pizza.addTopping(topping);
        if(!success) return false;
        pizzas.set(index, pizza);
        return true;
    }

    public boolean removeTopping(Pizza pizza, Topping topping) {
        int index = pizzas.indexOf(pizza);
        boolean success = pizza.removeTopping(topping);
        if(!success) return false;
        pizzas.set(index, pizza);
        return true;
    }
}
