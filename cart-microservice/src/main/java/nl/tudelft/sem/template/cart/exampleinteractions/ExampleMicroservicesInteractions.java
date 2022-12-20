package nl.tudelft.sem.template.cart.exampleinteractions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.checkout.domain.Order;
import nl.tudelft.sem.checkout.domain.OrderModel;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/exampleInteractions")
public class ExampleMicroservicesInteractions {

    // make sure cart + checkout are both running
    private RequestHelper requestsHelper;

    @GetMapping("/addOrder")
    public String addOrder() {
        OrderModel orderModel = new OrderModel(1, "Matt", LocalDateTime.of(LocalDate.of(2022, 5, 12), LocalTime.NOON), new ArrayList<>(), "ABCD12");
        return requestsHelper.postRequest(8082, "/orders/add", orderModel, String.class);
    }

    /**
     * Get all orders
     */
    @GetMapping("/getAllOrders")
    public List<Order> requestAllOrders() {
        List<Order> orders = requestsHelper.getRequest(8082, "/orders/all", List.class);
        return orders;
    }

}
