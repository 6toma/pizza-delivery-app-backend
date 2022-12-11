package nl.tudelft.sem.customer.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private CustomerRepository customerRepository;

    public Customer getCustomer(int customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void addToUsedCoupons(int customerId, String couponCode) {
        Customer customer = getCustomer(customerId);
        if(customer == null) { return; }

        List<String> coupons = customer.getUsedCoupons();
        coupons.add(couponCode);
        customer.setUsedCoupons(coupons);

        customerRepository.save(customer);
    }

    public void updateAllergens(int customerId, List<String> newToppings) {
        Customer customer = getCustomer(customerId);
        if(customer == null) { return; }

        List<String> toppings = customer.getAllergens();
        toppings.addAll(newToppings);
        customer.setAllergens(toppings);

        customerRepository.save(customer);
    }
}
