package nl.tudelft.sem.template.checkout.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import nl.tudelft.sem.template.checkout.domain.Order;
import nl.tudelft.sem.template.checkout.domain.OrderRepository;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.CartPizza;
import nl.tudelft.sem.testing.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("mockRequestHelper")
class OrderControllerTest extends IntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testGetOrdersCustomer() throws Exception {
        var pizza = new CustomPizza("pizza", 10, List.of(new Topping("salami", 5)));
        var order = orderRepository.save(
            Order.builder()
                .withCustomerId(TEST_USER)
                .withPizzaList(List.of(new CartPizza(pizza, 1)))
                .withPickupTime(LocalDateTime.now())
                .withStoreId(1L)
                .build()
        );
        var result = doRequest(authenticated(get("/orders/"))).andExpect(status().isOk());
        var pizzas = parseResponseJson(result, Order[].class);
        assertThat(pizzas.length).isOne();
        assertThat(pizzas[0].getOrderId()).isEqualTo(order.getOrderId());
    }

}