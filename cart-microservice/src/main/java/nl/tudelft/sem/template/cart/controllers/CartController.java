package nl.tudelft.sem.template.cart.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.cart.CartRepository;
import nl.tudelft.sem.template.cart.PizzaRepository;
import nl.tudelft.sem.template.cart.PizzaService;
import nl.tudelft.sem.template.cart.ToppingRepository;
import nl.tudelft.sem.template.commons.entity.Cart;
import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.PizzaToppingModel;
import nl.tudelft.sem.template.commons.models.ToppingModel;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private static final String CHECKOUT_URL = "http://localhost:8082/orders/add";


    // dependencies
    private final RequestHelper requestHelper;
    private final PizzaRepository pizzaRepository;
    private final PizzaService pizzaService;
    private final CartRepository cartRepository;
    private final AuthManager authManager;
    private final ToppingRepository toppingRepository;


    /**
     * Adds a pizza to your cart
     * @param pizzaName the name of the pizza to be added
     * @return a message based on whether adding the pizza was sucesful
     */
    @PostMapping("/addPizza")
    String addPizzaToCart(@RequestBody String pizzaName) {
        NetId netId = authManager.getNetIdObject();
        Cart cart = cartRepository.findByNetId(netId);
        if (cart == null) {
            cart = new Cart(netId, new ArrayList<>());
            cartRepository.save(cart);
        }
        var optionalPizza = pizzaService.getPizza(pizzaName);
        if (optionalPizza.isEmpty()) {
            return "Pizza name of " + pizzaName + " is not in the default pizza db";
        }
        Pizza pizza = optionalPizza.get();
        if (cart.getPizzas().contains(pizza)) {
            return "Pizza is already in cart";
        }
        cart.addPizza(pizza);
        cartRepository.save(cart);
        return "Pizza was added to the cart";
    }

    @PostMapping("/removePizza")
    String removePizzaFromCart(@RequestBody Pizza pizza) {
        NetId netId = authManager.getNetIdObject();
        Cart cart = cartRepository.findByNetId(netId);
        List<Pizza> pizzas = cart.getPizzas();
        if (!pizzas.contains(pizza)) {
            return "Pizza is not in your cart";
        }
        cart.removePizza(pizza);
        cartRepository.save(cart);
        return "Pizza was removed from your cart";
    }


    @PostMapping("/addTopping")
    String addToppingToPizza(@RequestBody PizzaToppingModel pizzaToppingModel) {
        Pizza pizza = pizzaToppingModel.getPizza();
        String toppingName = pizzaToppingModel.getTopping();
        Optional<Topping> t = toppingRepository.findByName(toppingName);
        if(t.isEmpty()) {
            return "That is not a valid topping";
        }
        Topping topping = t.get();
        NetId netId = authManager.getNetIdObject();
        Cart cart = cartRepository.findByNetId(netId);
        List<Pizza> pizzas = cart.getPizzas();
        if (!pizzas.contains(pizza)) {
            return "Pizza not found";
        }
        if(pizza.getToppings().contains(topping)) {
            return "This topping is already on the pizza";
        }
        cart.addTopping(pizza, topping);
        cartRepository.save(cart);
        return "Topping was added";
    }


    @PostMapping("/removeTopping")
    String removeToppingFromPizza(@RequestBody PizzaToppingModel pizzaToppingModel) {
        Pizza pizza = pizzaToppingModel.getPizza();
        String toppingName = pizzaToppingModel.getTopping();
        Optional<Topping> t = toppingRepository.findByName(toppingName);
        if(t.isEmpty()) {
            return "That is not a valid topping";
        }
        Topping topping = t.get();
        NetId netId = authManager.getNetIdObject();
        Cart cart = cartRepository.findByNetId(netId);
        List<Pizza> pizzas = cart.getPizzas();
        if (!pizzas.contains(pizza)) {
            return "Pizza not found";
        }
        if(!pizza.getToppings().contains(topping)) {
            return "This topping is not on the pizza";
        }
        cart.removeTopping(pizza, topping);
        cartRepository.save(cart);
        return "Topping was added";
    }

    @GetMapping("/getCart")
    Cart getCart(@RequestBody NetId netId) {
        return cartRepository.findByNetId(netId);
    }


    /**
     * Submits an order to a store, removing the items from the cart.
     *
     * @param storeId store to send the order to
     * @return a string that indicates if the order was placed or weather an error occured.
     */
    /*@PostMapping("/submitOrder")
    String selectPickupStore(@RequestBody int storeId) {
        // TODO check if cart is empty
        OrderModel orderModel = new OrderModel(storeId, pizzaInCart);
        ResponseEntity<String> outcome = requestHelper.postRequest(8082, "/orders/add", orderModel, String.class);


        if (Objects.equals(outcome.getBody(), "Order added")) {
            return String.format("order of %d pizzas was placed.", pizzaInCart.size());
        }
        return "Something went wrong!";
    }*/
}
