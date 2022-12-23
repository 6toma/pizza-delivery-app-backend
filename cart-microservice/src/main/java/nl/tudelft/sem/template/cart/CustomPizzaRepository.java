package nl.tudelft.sem.template.cart;

import nl.tudelft.sem.template.commons.entity.CustomPizza;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomPizzaRepository extends JpaRepository<CustomPizza, Integer> {
}
