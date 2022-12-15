package nl.tudelft.sem.template.commons.entity;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.commons.PizzaAttributeConverter;
import nl.tudelft.sem.template.commons.ToppingAttributeConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "carts")
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @ElementCollection
    @Column(name = "pizzas", nullable = false)
    @Convert(converter = PizzaAttributeConverter.class)
    private List<Pizza> pizzas;

}
