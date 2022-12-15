package nl.tudelft.sem.template.commons.entity;

import java.util.List;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.commons.PizzaAttributeConverter;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.NetIdAttributeConverter;
import org.springframework.beans.factory.annotation.Required;

@Entity
@Table(name = "carts")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    public Cart(NetId netId, List<Pizza> pizzas ){
        this.netId = netId;
        this.pizzas = pizzas;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "pizzas", nullable = false)
    @Convert(converter = PizzaAttributeConverter.class)
    private List<Pizza> pizzas;

    @Column(name = "netId", nullable = false, unique = true)
    @Convert(converter = NetIdAttributeConverter.class)
    private NetId netId;

}
