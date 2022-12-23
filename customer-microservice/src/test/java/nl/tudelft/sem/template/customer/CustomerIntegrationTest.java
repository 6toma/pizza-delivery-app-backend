package nl.tudelft.sem.template.customer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.customer.domain.Customer;
import nl.tudelft.sem.template.customer.domain.CustomerRepository;
import nl.tudelft.sem.testing.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerIntegrationTest extends IntegrationTest {


    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer1;

    @BeforeEach
    void setup() {
        customerRepository.deleteAll();
        customerRepository.flush();
        customer1 = customerRepository.save(new Customer(new NetId("someUser@gmail.com")));
    }

    @Test
    public void testGetCustomerById() throws Exception {
        getCustomerById(-1).andExpect(status().isNotFound());
    }

    @Test
    public void testGetCustomerByIdThatExists() throws Exception {
        getCustomerById(customer1.getCustomerId()).andExpect(status().isOk());
    }

    @Test
    public void testCustomerByNetId() throws Exception {
        getCustomerByNetId(customer1.getNetId().toString()).andExpect(status().isOk());
    }

    @Test
    public void testCustomerByNetIdNotThere() throws Exception {
        getCustomerByNetId("someUser@user.com").andExpect(status().isNotFound());
    }

    @Test
    public void testGetCustomersOne() throws Exception {
        var res = getAll().andExpect(status().isOk());
        var response = parseResponseJson(res, List.class);
        assertEquals(response.size(), 1);
    }

    @Test
    public void testAddCustomer() throws Exception {
        var res = postAdd("newCustomer@test.com").andExpect(status().isOk());
        assertEquals(customerRepository.findAll().size(), 2);
    }

    @Test
    public void testAddToUsedCoupon() throws Exception {
        postAddToUsedCoupons("someUser@gmail.com", "ABCD123").andExpect(status().isOk());
        postAddToUsedCoupons("someUser@gmail.com", "other").andExpect(status().isOk());
        Customer customer = customerRepository.findAll().get(0);
        assertThat(customer.getUsedCoupons().get(0)).isEqualTo("ABCD123");
        assertThat(customer.getUsedCoupons().get(1)).isEqualTo("other");
    }

    @Test
    public void testRemoveFromUsedCoupon() throws Exception {
        postAddToUsedCoupons("someUser@gmail.com", "ABCD123").andExpect(status().isOk());
        postRemoveFromUsedCoupons("someUser@gmail.com", "ABCD123").andExpect(status().isOk());
        Customer customer = customerRepository.findAll().get(0);
        assertThat(customer.getUsedCoupons().size()).isEqualTo(0);
    }

    @Test
    public void testHasUsedCouponTrue() throws Exception {
        postAddToUsedCoupons("someUser@gmail.com", "ABCD123").andExpect(status().isOk());
        postAddToUsedCoupons("someUser@gmail.com", "other").andExpect(status().isOk());

        getHasUsedCoupon("someUser@gmail.com", "ABCD123").andExpect(status().isOk())
            .andExpect(content().string("true"));
        getHasUsedCoupon("someUser@gmail.com", "other").andExpect(status().isOk())
            .andExpect(content().string("true"));
    }

    @Test
    public void testHasUsedCouponFalse() throws Exception {
        postAddToUsedCoupons("someUser@gmail.com", "ABCD123").andExpect(status().isOk());

        getHasUsedCoupon("someUser@gmail.com", "EFGH567").andExpect(status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    public void testCheckUsedCoupons() throws Exception {
        List<String> coupons = Arrays.asList("ABCD123", "AAAAA");
        postAddToUsedCoupons("someUser@gmail.com", "ABCD123").andExpect(status().isOk());
        var res = postCheckUsedCoupons("someUser@gmail.com", coupons).andExpect(status().isOk());
        String[] couponsArray = {"AAAAA"};
        res.andExpect(jsonPath("@", hasSize(1)))
            .andExpect(jsonPath("@", containsInAnyOrder(couponsArray)));
    }

    @Test
    public void testUpdateAllergens() throws Exception {
        postUpdateAllergens(List.of("salami", "tomato"));
        Customer customer = customerRepository.findAll().get(0);
        assertThat(customer.getAllergens().get(0)).isEqualTo("salami");
        assertThat(customer.getAllergens().get(1)).isEqualTo("tomato");
    }

    @Test
    public void testGetAllergens() throws Exception {
        postUpdateAllergens(List.of("salami", "tomato"));
        Customer customer = customerRepository.findByNetId(new NetId("someUser@gmail.com")).get();
        var res = getAllergens("someUser@gmail.com");

        res.andExpect(jsonPath("@", hasSize(2)))
            .andExpect(jsonPath("@", containsInAnyOrder("salami", "tomato")));
    }

    private ResultActions getCustomerById(int id) throws Exception {
        return mockMvc.perform(microservice(get("/customers/id/" + id)));
    }

    private ResultActions getCustomerByNetId(String netId) throws Exception {
        return mockMvc.perform(microservice(get("/customers/netId/" + netId)));
    }

    private ResultActions getAll() throws Exception {
        return mockMvc.perform(microservice(get("/customers/getAll/")));
    }

    private ResultActions postAdd(String netId) throws Exception {
        return mockMvc.perform(microservice(post("/customers/add/"))
            .contentType(MediaType.APPLICATION_JSON).content(netId));
    }

    private ResultActions postAddToUsedCoupons(String netId, String couponCode) throws Exception {
        return mockMvc.perform(microservice(post("/customers/" + netId + "/coupons/add/"))
            .contentType(MediaType.APPLICATION_JSON).content(couponCode));
    }

    private ResultActions postRemoveFromUsedCoupons(String netId, String couponCode) throws Exception {
        return mockMvc.perform(microservice(post("/customers/" + netId + "/coupons/remove"))
            .contentType(MediaType.APPLICATION_JSON).content(couponCode));
    }

    private ResultActions getHasUsedCoupon(String netId, String couponCode) throws Exception {
        return mockMvc.perform(microservice(get("/customers/" + netId + "/coupons/" + couponCode)));
    }

    private ResultActions postCheckUsedCoupons(String netId, List<String> couponCodes) throws Exception {
        return mockMvc.perform(microservice(post("/customers/checkUsedCoupons/" + netId))
            .contentType(MediaType.APPLICATION_JSON).content(toJson(couponCodes)));
    }

    private ResultActions postUpdateAllergens(List<String> toppings) throws Exception {
        return mockMvc.perform(authenticated(post("/customers/allergens"), "someUser@gmail.com")
            .contentType(MediaType.APPLICATION_JSON).content(toJson(toppings)));
    }

    private ResultActions getAllergens(String netId) throws Exception {
        return mockMvc.perform(microservice(get("/customers/allergens/" + netId)));
    }
}
