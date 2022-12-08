package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.CustomerRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
    private final CustomerRepository customerRepository;


}
