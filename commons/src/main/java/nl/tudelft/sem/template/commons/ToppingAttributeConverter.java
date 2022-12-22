package nl.tudelft.sem.template.commons;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import nl.tudelft.sem.template.commons.entity.Topping;

/**
 * Converter for the HashedPassword value object.
 */
@Converter
public class ToppingAttributeConverter implements AttributeConverter<Topping, String> {

    @Override
    public String convertToDatabaseColumn(Topping attribute) {
        return attribute.toString();
    }

    @Override
    public Topping convertToEntityAttribute(String dbData) {
        String[] arr = dbData.split(", ");
        String name = arr[0];
        double price = Double.parseDouble(arr[1]);
        return new Topping(name, price);
    }

}

