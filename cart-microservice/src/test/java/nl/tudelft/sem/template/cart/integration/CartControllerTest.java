package nl.tudelft.sem.template.cart.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collection;
import javax.transaction.Transactional;
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
import nl.tudelft.sem.testing.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@Transactional
public class CartControllerTest extends IntegrationTest {

    @Autowired
    private ToppingRepository toppingRepository;
    @Autowired
    private DefaultPizzaRepository defaultRepository;
    @Autowired
    private CustomPizzaRepository customRepository;

    @Autowired
    private CartRepository cartRepository;

    private DefaultPizza defaultPizza1;
    private DefaultPizza defaultPizza2;

    @BeforeEach
    void setup() {
        defaultRepository.deleteAll();
        customRepository.deleteAll();
        var toppings1 = toppingRepository.saveAll(Arrays.asList(new Topping("test1", 10),
            new Topping("test2", 13),
            new Topping("test3", 15)));
        cartRepository.deleteAll();
        defaultPizza1 = defaultRepository.saveAndFlush(new DefaultPizza("Default Pizza", toppings1, 10));

        var toppings2 = toppingRepository.saveAll(Arrays.asList(new Topping("test4", 7),
            new Topping("test5", 10),
            new Topping("test6", 13)));

        defaultPizza2 = defaultRepository.saveAndFlush(new DefaultPizza("Default Pizza 2", toppings2, 10));
    }


    @Test
    void testAddToCartSuccess() throws Exception {
        int id = defaultPizza1.getId();
        var result = addPizzaRequest(id);
        result.andExpect(status().isOk());
        assertEquals(1, customRepository.count());
        var custom = customRepository.findAll().stream().findFirst().get();
        assertEqualsPizzas(defaultPizza1, custom);
    }

    @Test
    void testAddToCartDifferentPizzas() throws Exception {
        int id1 = defaultPizza1.getId(), id2 = defaultPizza2.getId();
        addPizzaRequest(id1).andExpect(status().isOk());
        addPizzaRequest(id2).andExpect(status().isOk());
        assertEquals(2, customRepository.count());
        var pizzas = customRepository.findAll();
        assertEqualsPizzas(defaultPizza1, search(pizzas, defaultPizza1.getPizzaName()));
        assertEqualsPizzas(defaultPizza2, search(pizzas, defaultPizza2.getPizzaName()));
    }

    @Test
    void testAddToCartSamePizzas() throws Exception {
        int id = defaultPizza1.getId();
        addPizzaRequest(id).andExpect(status().isOk());
        addPizzaRequest(id).andExpect(status().isOk());
        assertEquals(2, customRepository.count());
        customRepository.findAll().forEach(pizza -> assertEqualsPizzas(defaultPizza1, pizza));
    }

    @Test
    void testAddToCartPizzaDoesntExist() throws Exception {
        addPizzaRequest(-1).andExpect(status().isBadRequest());
    }

    @Test
    void testIncrementPizza() throws Exception {
        int id = defaultPizza1.getId();
        var result = addPizzaRequest(id).andExpect(status().isOk());
        var customPizza = customRepository.findById(parseResponseInt(result)).get();

        incrementPizzaRequest(customPizza.getId()).andExpect(status().isOk());
        var cart = cartRepository.findAll().get(0);
        assertThat(cart.getPizzasMap().get(customPizza))
            .isEqualTo(2);
    }

    @Test
    void testIncrementEmptyCart() throws Exception {
        int id = defaultPizza1.getId();
        CustomPizza customPizza = CustomPizza.CustomPizzaCreator(defaultPizza1);
        customRepository.save(customPizza);

        incrementPizzaRequest(customPizza.getId()).andExpect(status().isBadRequest());
    }

    @Test
    void testIncrementNotInCart() throws Exception {
        CustomPizza customPizza = customRepository.save(CustomPizza.CustomPizzaCreator(defaultPizza1));
        addPizzaRequest(defaultPizza2.getId());
        incrementPizzaRequest(customPizza.getId()).andExpect(status().isBadRequest());
    }

    @Test
    void testIncrementPizzaDoesntExist() throws Exception {
        incrementPizzaRequest(123).andExpect(status().isBadRequest());
    }

    @Test
    void testDecrementPizza() throws Exception {
        int id = defaultPizza1.getId();
        var result = addPizzaRequest(id).andExpect(status().isOk());
        var customPizza = customRepository.findById(parseResponseInt(result)).get();
        incrementPizzaRequest(customPizza.getId()).andExpect(status().isOk());
        decrementPizzaRequest(customPizza.getId()).andExpect(status().isOk());

        var cart = cartRepository.findAll().get(0);
        assertThat(cart.getPizzasMap().get(customPizza)).isEqualTo(1);
    }

    @Test
    void testDecrementEmptyCart() throws Exception {
        int id = defaultPizza1.getId();
        CustomPizza customPizza = CustomPizza.CustomPizzaCreator(defaultPizza1);
        customRepository.save(customPizza);
        decrementPizzaRequest(customPizza.getId()).andExpect(status().isBadRequest());
    }

    @Test
    void testDecrementNotInCart() throws Exception {
        CustomPizza customPizza = CustomPizza.CustomPizzaCreator(defaultPizza1);
        customRepository.save(customPizza);
        addPizzaRequest(defaultPizza2.getId());
        decrementPizzaRequest(customPizza.getId()).andExpect(status().isBadRequest());
    }

    @Test
    void testDecrementPizzaDoesntExist() throws Exception {
        decrementPizzaRequest(123).andExpect(status().isBadRequest());
    }

    @Test
    void testDecrementDeletesPizza() throws Exception {
        int id = defaultPizza1.getId();
        int customPizzaId = parseResponseInt(addPizzaRequest(id).andExpect(status().isOk()));
        decrementPizzaRequest(customPizzaId).andExpect(status().isOk());
        assertEquals(0, customRepository.count());
    }

    @Test
    void testRemovePizza() throws Exception {
        int idPizza1 = defaultPizza1.getId();
        // add one pizza
        var result = addPizzaRequest(idPizza1).andExpect(status().isOk());

        int pizzaId = parseResponseInt(result);

        removePizzaRequest(pizzaId).andExpect(status().isOk());

        assertThat(customRepository.count()).isZero();
    }

    @Test
    void testRemovePizzaNotInCart() throws Exception {
        CustomPizza customPizza = CustomPizza.CustomPizzaCreator(defaultPizza1);
        customRepository.save(customPizza);
        addPizzaRequest(defaultPizza2.getId());
        removePizzaRequest(customPizza.getId()).andExpect(status().isBadRequest());
    }

    @Test
    void testRemovePizzaEmptyCart() throws Exception {
        int id = defaultPizza1.getId();
        CustomPizza customPizza = CustomPizza.CustomPizzaCreator(defaultPizza1);
        customRepository.save(customPizza);
        removePizzaRequest(customPizza.getId()).andExpect(status().isBadRequest());
    }

    @Test
    void testRemovePizzaDoesntExist() throws Exception {
        removePizzaRequest(123).andExpect(status().isBadRequest());
    }

    @Test
    void testAddTopping() throws Exception {
        var result = addPizzaRequest(defaultPizza1.getId()).andExpect(status().isOk());
        var pizzaId = parseResponseInt(result);
        var topping = toppingRepository.save(new Topping("Test topping", 100));
        addToppingRequest(pizzaId, topping.getId()).andExpect(status().isOk());
        var customPizza = customRepository.findById(pizzaId).get();
        assertTrue(customPizza.getToppings().contains(topping));
    }

    @Test
    void testToppingDoesNotExist() throws Exception {
        var result = addPizzaRequest(defaultPizza1.getId()).andExpect(status().isOk());
        var pizzaId = parseResponseInt(result);
        addToppingRequest(pizzaId, -1).andExpect(status().isBadRequest());
    }

    @Test
    void testToppingAlreadyOnPizza() throws Exception {
        var result = addPizzaRequest(defaultPizza1.getId()).andExpect(status().isOk());
        var pizzaId = parseResponseInt(result);
        var topping = defaultPizza1.getToppings().get(0);
        addToppingRequest(pizzaId, topping.getId()).andExpect(status().isBadRequest());
    }

    @Test
    void testPizzaDoesNotExist() throws Exception {
        addPizzaRequest(defaultPizza1.getId());
        var topping = toppingRepository.save(new Topping("Test topping", 100));
        addToppingRequest(-1, topping.getId()).andExpect(status().isBadRequest());
    }

    @Test
    void testAddToppingPizzaNotInCart() throws Exception {
        CustomPizza customPizza = CustomPizza.CustomPizzaCreator(defaultPizza1);
        customRepository.save(customPizza);
        addPizzaRequest(defaultPizza2.getId()).andExpect(status().isOk());

        var topping = toppingRepository.save(new Topping("Test topping", 100));
        addToppingRequest(customPizza.getId(), topping.getId()).andExpect(status().isBadRequest());
    }

    @Test
    void testAddToppingDoesntExist() throws Exception {
        var result = addPizzaRequest(defaultPizza1.getId()).andExpect(status().isOk());
        var pizzaId = parseResponseInt(result);
        addToppingRequest(pizzaId, -1).andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveTopping() throws Exception {
        var result = addPizzaRequest(defaultPizza1.getId()).andExpect(status().isOk());
        var pizzaId = parseResponseInt(result);
        var topping = defaultPizza1.getToppings().get(0);
        removeToppingRequest(pizzaId, topping.getId()).andExpect(status().isOk());
        var customPizza = customRepository.findById(pizzaId).get();
        assertFalse(customPizza.getToppings().contains(topping));
    }

    @Test
    void testRemoveToppingNotOnPizza() throws Exception {
        var result = addPizzaRequest(defaultPizza1.getId()).andExpect(status().isOk());
        var pizzaId = parseResponseInt(result);
        var topping2 = toppingRepository.save(new Topping("abc", 200));
        removeToppingRequest(pizzaId, topping2.getId()).andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveToppingPizzaDoesntExist() throws Exception {
        addPizzaRequest(defaultPizza1.getId()).andExpect(status().isOk());
        var topping = toppingRepository.save(new Topping("Test topping", 100));
        removeToppingRequest(-1, topping.getId()).andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveToppingPizzaNotInCart() throws Exception {
        CustomPizza customPizza = CustomPizza.CustomPizzaCreator(defaultPizza1);
        customRepository.save(customPizza);
        addPizzaRequest(defaultPizza2.getId());
        var topping = toppingRepository.save(new Topping("Test topping", 100));
        removeToppingRequest(customPizza.getId(), topping.getId()).andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveToppingDoesntExist() throws Exception {
        var result = addPizzaRequest(defaultPizza1.getId()).andExpect(status().isOk());
        var pizzaId = parseResponseInt(result);
        removeToppingRequest(pizzaId, -1).andExpect(status().isBadRequest());
    }

    @Test
    void testGetCart() throws Exception {
        addPizzaRequest(defaultPizza1.getId());
        addPizzaRequest(defaultPizza2.getId());
        var cart = cartRepository.findAll().get(0);
        var result = getCartRequest(new NetId(TEST_USER)).andExpect(status().isOk());
        var cartPizzas = Arrays.asList(parseResponseJson(result, CartPizza[].class));
        assertThat(cartRepository.count()).isZero();
        assertEquals(cart.getPizzasMap().size(), cartPizzas.size());
        cartPizzas.forEach(cp -> {
            var pizzaInMap =
                cart.getPizzasMap().entrySet().stream().filter(e -> e.getKey().getId() == cp.getPizza().getId()).findFirst();
            assertTrue(pizzaInMap.isPresent());
            assertEquals(pizzaInMap.get().getValue(), cp.getAmount());
        });
    }

    @Test
    void testGetCartNoCartForId() throws Exception {
        getCartRequest(new NetId("Not a real ID")).andExpect(status().isBadRequest());
    }

    private CustomPizza search(Collection<CustomPizza> pizzas, String name) {
        return pizzas.stream().filter(p -> p.getPizzaName().equals(name)).findFirst().get();
    }

    private void assertEqualsPizzas(DefaultPizza defaultPizza, CustomPizza customPizza) {
        assertThat(customPizza.getToppings()).hasSameElementsAs(defaultPizza.getToppings());
    }

    private ResultActions addPizzaRequest(int pizzaId) throws Exception {
        return mockMvc.perform(authenticated(post("/cart/addPizza/" + pizzaId)));
    }

    public ResultActions incrementPizzaRequest(int pizzaId) throws Exception {
        return mockMvc.perform(authenticated(post("/cart/incrementPizza/" + pizzaId)));
    }

    public ResultActions decrementPizzaRequest(int pizzaId) throws Exception {
        return mockMvc.perform(authenticated(post("/cart/decrementPizza/" + pizzaId)));
    }

    private ResultActions removePizzaRequest(int pizzaId) throws Exception {
        return mockMvc.perform(authenticated(post("/cart/removePizza/" + pizzaId)));
    }

    private ResultActions getCartRequest(NetId netId) throws Exception {
        return mockMvc.perform(authenticated(get("/cart/getCart/" + netId)));
    }

    private ResultActions addToppingRequest(int pizzaId, int toppingId) throws Exception {
        PizzaToppingModel ptm = new PizzaToppingModel();
        ptm.setToppingId(toppingId);
        ptm.setPizzaId(pizzaId);
        return mockMvc.perform(authenticated(post("/cart/addTopping/"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(ptm)));
    }

    private ResultActions removeToppingRequest(int pizzaId, int toppingId) throws Exception {
        PizzaToppingModel ptm = new PizzaToppingModel();
        ptm.setToppingId(toppingId);
        ptm.setPizzaId(pizzaId);
        return mockMvc.perform(authenticated(post("/cart/removeTopping/"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(ptm)));
    }

}