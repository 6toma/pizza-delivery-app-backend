package nl.tudelft.sem.template.cart.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nl.tudelft.sem.template.cart.PizzaRepository;
import nl.tudelft.sem.template.checkout.OrderModel;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private static final String CHECKOUT_URL = "http://localhost:8082/orders/add";

    private final List<Pizza> pizzaInCart = new ArrayList<>();

    // dependencies
    private final RequestHelper requestHelper;
    private final PizzaRepository pizzaRepository;

    CartController(RequestHelper requestHelper, PizzaRepository pizzaRepository) {
        this.requestHelper = requestHelper;
        this.pizzaRepository = pizzaRepository;
    }

    /**
     * Check if a given pizza is a custom pizza or a default one.
     *
     * @param pizza pizza to check
     * @return true/false if the pizza is custom
     */
    boolean isPizzaCustom(Pizza pizza) {
        return (pizza.getPizzaName().equals("Custom"));
    }

    /**
     * Adds pizza to the cart. Checks if the pizza provided is properly validated with <i>@Validation</i> annotation
     * <p>
     * If the validation fails then we can get the error messages nicely in postman too. If the pizza is not custom, I think
     * it makes sense to enforce the rule that the pizza name should be a default pizza.
     *
     * @param pizza         pizza to add to cart
     * @param bindingResult spring binded this class so that we can check what were the validation errors
     * @return
     */
    @PostMapping("/addPizza")
    String addPizzaToCart(@RequestBody @Validated Pizza pizza, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String defaultMessage = bindingResult.getFieldError().getDefaultMessage();
            return Objects.requireNonNullElse(defaultMessage, "Error in creating Pizza");
        }
        // if is not a custom pizza and we do not have it as a default pizza we can't add it to cart
        if (!isPizzaCustom(pizza) && !pizzaRepository.existsByPizzaName(pizza.getPizzaName())) {
            return "The pizza name '" + pizza.getPizzaName() + "' is not in the default pizza db";
        }

        // add pizza to current cart
        pizzaInCart.add(pizza);
        return "Pizza " + pizza + " was added to cart.";
    }


    /**
     * Submits an order to a store, removing the items from the cart.
     *
     * @param storeId store to send the order to
     * @return a string that indicates if the order was placed or weather an error occured.
     */
    @PostMapping("/submitOrder")
    String selectPickupStore(@RequestBody int storeId) {
        // TODO check if cart is empty
        OrderModel orderModel = new OrderModel(storeId, pizzaInCart);
        ResponseEntity<String> outcome = requestHelper.postRequest(8082, "/orders/add", orderModel, String.class);


        if (Objects.equals(outcome.getBody(), "Order added")) {
            return String.format("order of %d pizzas was placed.", pizzaInCart.size());
        }
        return "Something went wrong!";
    }
}
