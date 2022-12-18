package nl.tudelft.sem.template.cart.controllers;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.cart.*;
import nl.tudelft.sem.template.commons.entity.Cart;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.PizzaToppingModel;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    // dependencies
    private final RequestHelper requestHelper;
    private final DefaultPizzaRepository defaultPizzaRepository;
    private final CustomPizzaRepository customPizzaRepository;
    private final PizzaService pizzaService;
    private final CartRepository cartRepository;
    private final AuthManager authManager;
    private final ToppingRepository toppingRepository;


    private Cart getCartFromNetId() {
        NetId netId = authManager.getNetIdObject();
        return cartRepository.findByNetId(netId);
    }

    private CustomPizza getDefaultPizza(int defaultPizzaId) {
        DefaultPizza pizza = requireNotEmpty(defaultPizzaRepository.findById(defaultPizzaId),
                "Custom pizza not found with id " + defaultPizzaId);
        return CustomPizza.CustomPizzaCreator(pizza);
    }

    private CustomPizza getCustomPizza(int customPizzaId) {
        return requireNotEmpty(customPizzaRepository.findById(customPizzaId),
                "Custom pizza not found with id " + customPizzaId);
    }

    private Topping getTopping(int toppingId) {
        return requireNotEmpty(toppingRepository.findById(toppingId),
                "Topping not found with id " + toppingId);

    }

    private <T> T requireNotEmpty(Optional<T> optional, String message) {
        if (optional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, message);
        }
        return optional.get();
    }

    @PostMapping("/addPizza/{id}")
    String addPizzaToCart(@PathVariable("id") int defaultPizzaId) {
        CustomPizza customPizza = getCustomPizza(defaultPizzaId);
        Cart cart = getCartFromNetId();
        if (cart == null) {
            HashMap<CustomPizza, Integer> map = new HashMap<>();
            cart = new Cart(authManager.getNetIdObject(), map);
            cartRepository.save(cart);
        }
        cart.addPizza(customPizza);
        cartRepository.save(cart);
        return "Pizza was added to the cart";
    }

    @PostMapping("/removePizza/{id}")
    ResponseEntity<String> removePizzaFromCart(@PathVariable("id") int customPizzaId) {
        Cart cart = getCartFromNetId();
        if (cart == null) {
            return ResponseEntity.badRequest().body("You don't currently have a cart");
        }
        CustomPizza customPizza = getCustomPizza(customPizzaId);
        if (!cart.removePizza(customPizza)) {
            return ResponseEntity.badRequest().body("Can't remove pizza from cart");
        }
        customPizzaRepository.delete(customPizza);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/addTopping")
    ResponseEntity<String> addToppingToPizza(@RequestBody PizzaToppingModel pizzaToppingModel) {
        var pizza = getCustomPizza(pizzaToppingModel.getPizzaId());
        var topping = getTopping(pizzaToppingModel.getToppingId());

        if (pizza.addTopping(topping)) {
            customPizzaRepository.save(pizza);
        } else {
            return ResponseEntity.badRequest().body("You have already added this topping");
        }
        return ResponseEntity.ok("Topping was added");
    }


    @PostMapping("/removeTopping")
    ResponseEntity<String> removeToppingFromPizza(@RequestBody PizzaToppingModel pizzaToppingModel) {
        var pizza = getCustomPizza(pizzaToppingModel.getPizzaId());
        var topping = getTopping(pizzaToppingModel.getToppingId());
        if (pizza.removeTopping(topping)) {
            customPizzaRepository.save(pizza);
        }
        return ResponseEntity.ok().build();
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
