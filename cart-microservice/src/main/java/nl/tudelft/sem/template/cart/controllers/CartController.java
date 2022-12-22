package nl.tudelft.sem.template.cart.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.cart.CartRepository;
import nl.tudelft.sem.template.cart.CustomPizzaRepository;
import nl.tudelft.sem.template.cart.DefaultPizzaRepository;
import nl.tudelft.sem.template.cart.ToppingRepository;
import nl.tudelft.sem.template.commons.entity.Cart;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.entity.DefaultPizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.CartPizza;
import nl.tudelft.sem.template.commons.models.PizzaToppingModel;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    // dependencies
    private final RequestHelper requestHelper;
    private final DefaultPizzaRepository defaultPizzaRepository;
    private final CustomPizzaRepository customPizzaRepository;
    private final CartRepository cartRepository;
    private final AuthManager authManager;
    private final ToppingRepository toppingRepository;

    private Cart getCartFromSessionNetId() {
        NetId netId = authManager.getNetIdObject();
        Optional<Cart> optionalCart = cartRepository.findById(netId);
        if (optionalCart.isEmpty()) {
            return null;
        }
        return optionalCart.get();
    }

    private CustomPizza getDefaultPizza(int defaultPizzaId) {
        DefaultPizza pizza = requireNotEmpty(defaultPizzaRepository.findById(defaultPizzaId),
            "Custom pizza not found with id " + defaultPizzaId);
        return CustomPizza.customPizzaCreator(pizza);
    }

    private CustomPizza getCustomPizza(int customPizzaId) {
        return requireNotEmpty(customPizzaRepository.findById(customPizzaId),
            "Custom pizza not found with id " + customPizzaId);
    }

    private Topping getTopping(int toppingId) {
        return requireNotEmpty(toppingRepository.findById(toppingId), "Topping not found with id " + toppingId);

    }

    private <T> T requireNotEmpty(Optional<T> optional, String message) {
        if (optional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return optional.get();
    }

    private void assertInCart(Cart cart, CustomPizza pizza) {
        if (!cart.getPizzasMap().containsKey(pizza)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pizza is not in cart" + pizza);
        }
    }


    private Cart getCart() {
        Cart cart = getCartFromSessionNetId();
        if (cart == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You don't currently have a cart");
        }
        return cart;
    }

    @PostMapping("/addPizza/{id}")
    @Transactional
    int addPizzaToCart(@PathVariable("id") int defaultPizzaId) {
        CustomPizza customPizza = getDefaultPizza(defaultPizzaId);
        Cart cart = getCartFromSessionNetId();
        if (cart == null) {
            var id = authManager.getNetIdObject();
            cart = new Cart(id, new HashMap<>());
        }
        customPizza = customPizzaRepository.save(customPizza);
        cart.addPizza(customPizza);
        cartRepository.save(cart);
        return customPizza.getId();
    }

    @PostMapping("/incrementPizza/{id}")
    ResponseEntity<String> incrementPizza(@PathVariable("id") int customPizzaId) {
        CustomPizza customPizza = getCustomPizza(customPizzaId);
        Cart cart = getCart();
        assertInCart(cart, customPizza);
        cart.addPizza(customPizza);
        cartRepository.save(cart);
        return ResponseEntity.ok("Changed amount to " + cart.getPizzasMap().get(customPizza));
    }

    @PostMapping("/decrementPizza/{id}")
    ResponseEntity<String> decrementPizza(@PathVariable("id") int customPizzaId) {
        CustomPizza customPizza = getCustomPizza(customPizzaId);
        Cart cart = getCart();
        assertInCart(cart, customPizza);
        if (!cart.removePizza(customPizza)) {
            customPizzaRepository.delete(customPizza);
        }
        cartRepository.save(cart);
        return ResponseEntity.ok("Changed amount to " + cart.getPizzasMap().get(customPizza));
    }

    @PostMapping("/removePizza/{id}")
    ResponseEntity<String> removePizzaFromCart(@PathVariable("id") int customPizzaId) {
        Cart cart = getCart();
        CustomPizza customPizza = getCustomPizza(customPizzaId);
        assertInCart(cart, customPizza);
        cart.removePizzaAll(customPizza);
        customPizzaRepository.delete(customPizza);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addTopping")
    ResponseEntity<String> addToppingToPizza(@RequestBody PizzaToppingModel pizzaToppingModel) {
        var pizza = getCustomPizza(pizzaToppingModel.getPizzaId());
        var topping = getTopping(pizzaToppingModel.getToppingId());
        Cart cart = getCart();
        assertInCart(cart, pizza);
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
        Cart cart = getCart();
        assertInCart(cart, pizza);
        if (pizza.removeTopping(topping)) {
            customPizzaRepository.save(pizza);
        } else {
            return ResponseEntity.badRequest().body("This topping is not on the pizza");
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Gets the cart and clears the cart contents at the same time.
     *
     * @param netId netId of the user
     * @return the cart
     */
    @GetMapping("/getCart/{netId}")
    List<CartPizza> getCartFromNetId(@PathVariable("netId") NetId netId) {
        Optional<Cart> cartOptional = cartRepository.findById(netId);
        if (cartOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user doesn't have a cart");
        }
        Cart cart = cartOptional.get();
        cartRepository.deleteById(netId);
        return cart.getPizzasMap().entrySet().stream().map(entry -> new CartPizza(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
}
