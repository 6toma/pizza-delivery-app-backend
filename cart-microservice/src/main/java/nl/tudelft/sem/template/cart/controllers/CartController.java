package nl.tudelft.sem.template.cart.controllers;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.checkout.OrderModel;
import nl.tudelft.sem.template.commons.entity.Pizza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static String CHECKOUT_URL = "localhost:8082/";
    private static Logger logger = LoggerFactory.getLogger(CartController.class);
    private List<Pizza> pizzaInCart = new ArrayList<>();

    @PostMapping("/addPizza/")
    String addPizzaToCart(@RequestBody Pizza pizza) {
        // add pizza to current cart
        pizzaInCart.add(pizza);
        return "Pizza " + pizza.getPizzaName() + " was added to cart.";
    }

    @PostMapping("/submitOrder")
    void selectPickupStore(int storeId) {
        OrderModel orderModel = new OrderModel(storeId, pizzaInCart);
        String outcome = new RestTemplate().getForObject(CHECKOUT_URL, String.class, orderModel);
        logger.info("Sending order with " + pizzaInCart.size() + " pizzas.");
        logger.info("Outcome is : " + outcome);

    }
}
