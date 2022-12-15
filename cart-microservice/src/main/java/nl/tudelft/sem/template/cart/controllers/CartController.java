package nl.tudelft.sem.template.cart.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.cart.CartRepository;
import nl.tudelft.sem.template.cart.PizzaRepository;
import nl.tudelft.sem.template.cart.PizzaService;
import nl.tudelft.sem.template.checkout.OrderModel;
import nl.tudelft.sem.template.commons.entity.Pizza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private static final String CHECKOUT_URL = "http://localhost:8082/orders/add";

    private final List<Pizza> pizzaInCart = new ArrayList<>();

    // dependencies
    private final AuthManager authManager;
    private final PizzaService pizzaService;
    private final CartRepository cartRepository;

    CartController(AuthManager authManager, PizzaService pizzaService, CartRepository cartRepository) {
        this.authManager = authManager;
        this.pizzaService = pizzaService;
        this.cartRepository = cartRepository;
    }


    /**
     * Adds pizza to the cart. Checks if the pizza provided is properly validated with <i>@Validation</i> annotation
     * <p>
     * If the validation fails then we can get the error messages nicely in postman too. If the pizza is not custom, I think
     * it makes sense to enforce the rule that the pizza name should be a default pizza.
     *
     * @param pizzaName name of the pizza to be added to cart
     * @return
     */
    @PostMapping("/addPizza")
    String addPizzaToCart(@RequestBody String pizzaName) {
        if(pizzaService.checkPizzaIsUnique(pizzaName)) return "Pizza not found";
        Pizza pizza = pizzaService.getPizza(pizzaName).get();
        return "Pizza " + pizza + " was added to cart.";
    }

    /**
     * Does a post request to the Checkout microservice with the orderModel. It also passes the token for authentication,
     * otherwise the request will not be authorized by Checkout.
     * TODO: make this more generic so that all microservices can use it and move it to a commons utility function.
     *
     * @param orderModel    the orderModel that we send
     * @param responseClass the class type of the response we expect. In my case is <code>String.class</code>
     * @param <T>           the thing we want to get back, in this case a String.class. Maybe in the future we could also get
     *                      a list of objects
     * @return a spring Response entity
     */
    <T> ResponseEntity<T> postRequest(OrderModel orderModel, Class<T> responseClass) {
        var request = RequestEntity.post(URI.create(CHECKOUT_URL))
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + authManager.getJwtToken())
            .body(orderModel);
        var restTemplate = new RestTemplate();
        return restTemplate.exchange(request, responseClass);
    }

    /**
     * Submits an order to a store, removing the items from the cart.
     *
     * @param storeId store to send the order to
     * @return a string that indicates if the order was placed or weather an error occured.
     */
    @PostMapping("/submitOrder")
    String selectPickupStore(@RequestBody int storeId) {
        OrderModel orderModel = new OrderModel(storeId, pizzaInCart);
        ResponseEntity<String> outcome;
        try {
            outcome = postRequest(orderModel, String.class);
        } catch (ResourceAccessException connectException) {
            return "The checkout order microservice refused to connect. Check if it is running.";
        }
        logger.info("Sending order with " + pizzaInCart.size() + " pizzas.");
        logger.info("Outcome is : " + outcome.getBody());
        if (Objects.equals(outcome.getBody(), "Order added")) {
            return String.format("order of %d pizzas was placed.", pizzaInCart.size());
        }
        return "Something went wrong!";
    }
}
