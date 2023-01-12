package nl.tudelft.sem.template.checkout.domain;

import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.checkout.domain.Order;
import nl.tudelft.sem.template.checkout.domain.OrderRepository;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.CartPizza;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDataLoader implements ApplicationRunner {

    private final OrderRepository repository;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        var salami =
            new Topping("salami", 2);
        var bacon = new Topping("bacon", 2.5);
        var pineapple = new Topping("pineapple", 1.5);
        var pizza1 = new CustomPizza(
            "Pizza Hawaii",
            5,
            Arrays.asList(bacon, pineapple)
        );
        var pizza2 = new CustomPizza(
            "Meatlover",
            6,
            Arrays.asList(salami, bacon)
        );

        repository.save(
            Order.builder()
                .withStoreId(1)
                .withPickupTime(LocalDateTime.now().plusDays(30))
                .withCustomerId("otheruser@test.com")
                .withPizzaList(
                    Arrays.asList(
                        new CartPizza(pizza1, 1),
                        new CartPizza(pizza2, 1)
                    )
                )
                .withFinalPrice(pizza1.calculatePrice() + pizza2.calculatePrice())
                .build()
        );
    }
}
