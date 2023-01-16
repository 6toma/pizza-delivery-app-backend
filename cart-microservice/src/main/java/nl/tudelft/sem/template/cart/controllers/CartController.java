package nl.tudelft.sem.template.cart.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.annotations.role.RoleCustomer;
import nl.tudelft.sem.template.cart.CartRepository;
import nl.tudelft.sem.template.cart.CustomPizzaRepository;
import nl.tudelft.sem.template.cart.models.AddToCartResponse;
import nl.tudelft.sem.template.cart.services.CartService;
import nl.tudelft.sem.template.commons.entity.Cart;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.models.CartPizza;
import nl.tudelft.sem.template.commons.models.PizzaToppingModel;
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
    private final CustomPizzaRepository customPizzaRepository;
    private final CartRepository cartRepository;
    private final AuthManager authManager;
    private final CartService cartService;

    @GetMapping({"/", ""})
    @RoleCustomer
    List<CartPizza> getUserCart() {
        return cartService.convertPizzaMap(cartService.getCart().getPizzasMap());
    }

    @PostMapping("/addPizza/{id}")
    @Transactional
    AddToCartResponse addPizzaToCart(@PathVariable("id") int defaultPizzaId) {
        CustomPizza customPizza = cartService.getDefaultPizza(defaultPizzaId);
        Cart cart = cartService.getCartFromSessionNetId();
        if (cart == null) {
            var id = authManager.getNetIdObject();
            cart = new Cart(id, new HashMap<>());
        }
        customPizza = customPizzaRepository.save(customPizza);
        cart.addPizza(customPizza);
        cartRepository.save(cart);
        var allergens = cartService.getUserAllergens(authManager.getNetId());
        var containsAllergens = customPizza.getToppings().stream().anyMatch(t -> allergens.contains(t.getName()));
        return new AddToCartResponse(customPizza.getId(), containsAllergens);
    }

    @PostMapping("/incrementPizza/{id}")
    ResponseEntity<String> incrementPizza(@PathVariable("id") int customPizzaId) {
        CustomPizza customPizza = cartService.getCustomPizza(customPizzaId);
        Cart cart = cartService.getCart();
        cartService.assertInCart(cart, customPizza);
        cart.addPizza(customPizza);
        cartRepository.save(cart);
        return ResponseEntity.ok("Changed amount to " + cart.getPizzasMap().get(customPizza));
    }

    @PostMapping("/decrementPizza/{id}")
    ResponseEntity<String> decrementPizza(@PathVariable("id") int customPizzaId) {
        CustomPizza customPizza = cartService.getCustomPizza(customPizzaId);
        Cart cart = cartService.getCart();
        cartService.assertInCart(cart, customPizza);
        if (!cart.removePizza(customPizza)) {
            customPizzaRepository.delete(customPizza);
        }
        cartRepository.save(cart);
        return ResponseEntity.ok("Changed amount to " + cart.getPizzasMap().get(customPizza));
    }

    @PostMapping("/removePizza/{id}")
    ResponseEntity<String> removePizzaFromCart(@PathVariable("id") int customPizzaId) {
        Cart cart = cartService.getCart();
        CustomPizza customPizza = cartService.getCustomPizza(customPizzaId);
        cartService.assertInCart(cart, customPizza);
        cart.removePizzaAll(customPizza);
        customPizzaRepository.delete(customPizza);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addTopping")
    ResponseEntity<String> addToppingToPizza(@RequestBody PizzaToppingModel pizzaToppingModel) {
        var pizza = cartService.getCustomPizza(pizzaToppingModel.getPizzaId());
        var topping = cartService.getTopping(pizzaToppingModel.getToppingId());
        Cart cart = cartService.getCart();
        cartService.assertInCart(cart, pizza);
        if (pizza.addTopping(topping)) {
            customPizzaRepository.save(pizza);
        } else {
            return ResponseEntity.badRequest().body("You have already added this topping");
        }
        return ResponseEntity.ok("Topping was added");
    }


    @PostMapping("/removeTopping")
    ResponseEntity<String> removeToppingFromPizza(@RequestBody PizzaToppingModel pizzaToppingModel) {
        var pizza = cartService.getCustomPizza(pizzaToppingModel.getPizzaId());
        var topping = cartService.getTopping(pizzaToppingModel.getToppingId());
        Cart cart = cartService.getCart();
        cartService.assertInCart(cart, pizza);
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
        return cartService.convertPizzaMap(cart.getPizzasMap());
    }

}
