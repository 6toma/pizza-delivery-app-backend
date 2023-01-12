package nl.tudelft.sem.template.cart;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PizzaAndToppingDataLoader implements ApplicationRunner {

    private final ToppingRepository toppingRepository;
    private final DefaultPizzaRepository defaultPizzaRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO Add default pizzas and toppings
        var salami =
            new Topping("salami", 2);
        var bacon = new Topping("bacon", 2.5);
        var pineapple = new Topping("pineapple", 1.5);

        defaultPizzaRepository.saveAll(
            Arrays.asList(
                new DefaultPizza(
                    "Pizza Hawaii",
                    Arrays.asList(bacon, pineapple),
                    5
                ),
                new DefaultPizza(
                    "Meatlover",
                    Arrays.asList(salami, bacon),
                    6
                )
            )
        );
    }
}
