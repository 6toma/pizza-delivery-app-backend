package nl.tudelft.sem.template.cart.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
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
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import nl.tudelft.sem.template.commons.utils.RequestObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class CartService {

    private final CustomPizzaRepository customPizzaRepository;
    private final DefaultPizzaRepository defaultPizzaRepository;
    private final CartRepository cartRepository;
    private final AuthManager authManager;
    private final ToppingRepository toppingRepository;
    private final RequestHelper requestHelper;

    public Cart getCartFromSessionNetId() {
        NetId netId = authManager.getNetIdObject();
        Optional<Cart> optionalCart = cartRepository.findById(netId);
        if (optionalCart.isEmpty()) {
            return null;
        }
        return optionalCart.get();
    }

    public CustomPizza getDefaultPizza(int defaultPizzaId) {
        DefaultPizza pizza = requireNotEmpty(defaultPizzaRepository.findById(defaultPizzaId),
            "Custom pizza not found with id " + defaultPizzaId);
        return CustomPizza.customPizzaCreator(pizza);
    }

    public CustomPizza getCustomPizza(int customPizzaId) {
        return requireNotEmpty(customPizzaRepository.findById(customPizzaId),
            "Custom pizza not found with id " + customPizzaId);
    }

    public Topping getTopping(int toppingId) {
        return requireNotEmpty(toppingRepository.findById(toppingId), "Topping not found with id " + toppingId);

    }

    public <T> T requireNotEmpty(Optional<T> optional, String message) {
        if (optional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return optional.get();
    }

    public void assertInCart(Cart cart, CustomPizza pizza) {
        if (!cart.getPizzasMap().containsKey(pizza)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pizza is not in cart" + pizza);
        }
    }


    public Cart getCart() {
        Cart cart = getCartFromSessionNetId();
        if (cart == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You don't currently have a cart");
        }
        return cart;
    }

    public List<CartPizza> convertPizzaMap(Map<CustomPizza, Integer> map) {
        return map.entrySet().stream().map(entry -> new CartPizza(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    public Set<String> getUserAllergens(String netId) {
        try {
            return Arrays.stream(requestHelper.doRequest(new RequestObject(
                    HttpMethod.GET, 8081, "/customers/allergens/" + netId), String[].class))
                .collect(Collectors.toSet());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }
}
