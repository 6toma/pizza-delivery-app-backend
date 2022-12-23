package nl.tudelft.sem.template.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.persistence.AttributeConverter;
import nl.tudelft.sem.template.commons.models.CartPizza;

public class CartPizzaAttributeConverter implements AttributeConverter<CartPizza, String> {

    private transient final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(CartPizza attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CartPizza convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, CartPizza.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}