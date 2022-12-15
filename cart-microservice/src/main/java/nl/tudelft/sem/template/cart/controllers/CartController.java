package nl.tudelft.sem.template.cart.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping("/addPizza")
    String addPizzaToCart(@RequestBody String pizzaName) {
        NetId netId = authManager.getNetIdObject();
        Cart cart = cartRepository.findByNetId(netId);
        boolean cartWasNull = false;
        if (cart == null){
            cart = new Cart(netId, new ArrayList<>());
            cartWasNull = true;
        }

        Pizza pizza = pizzaService.getPizza(pizzaName).get();
        if (cart.getPizzas().contains(pizza)) {
            return "Pizza not found";
        }
        if(cartWasNull) {
            cartRepository.deleteByNetId(netId);
        }
        cart.getPizzas().add(pizza);
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
        pizzas.remove(pizza);
        return "Pizza was removed from your cart";
    }


    @PostMapping("/addTopping")
    String addToppingToPizza(@RequestBody Topping topping, @RequestBody Pizza pizza) {
        NetId netId = authManager.getNetIdObject();
        Cart cart = cartRepository.findByNetId(netId);
        List<Pizza> pizzas = cart.getPizzas();
        if (!pizzas.contains(pizza)) {
            return "Pizza not found";
        }
        pizza.addTopping(topping);
        pizzas.add(pizza);
        return "Topping was added";
    }


    @PostMapping("/removeTopping")
    String removeToppingFromPizza(@RequestBody Pizza pizza) {
        NetId netId = authManager.getNetIdObject();
        Cart cart = cartRepository.findByNetId(netId);
        List<Pizza> pizzas = cart.getPizzas();
        if (!pizzas.contains(pizza)) {
            return "Pizza is not in your cart";
        }
        pizzas.remove(pizza);
        return "Pizza was removed from your cart";
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
