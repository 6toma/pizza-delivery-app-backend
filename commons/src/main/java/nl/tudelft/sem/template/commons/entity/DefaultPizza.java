package nl.tudelft.sem.template.commons.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "default_pizza")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "pizzaName")
public class DefaultPizza extends Pizza {

    @Column(name = "name", nullable = false, unique = true)
    @NotNull(message = "Pizza name can't be null")
    @Size(min = 5, max = 30, message = "Pizza name should be between 5 and 30 characters long")
    @Getter
    private String pizzaName;


    public DefaultPizza(String pizzaName, List<Topping> toppingList, double price) {
        super(price, toppingList);
        this.pizzaName = pizzaName;
    }
}
