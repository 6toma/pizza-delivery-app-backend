package nl.tudelft.sem.template.commons;

import nl.tudelft.sem.template.commons.entity.Pizza;
import nl.tudelft.sem.template.commons.entity.Topping;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;

public class PizzaAttributeConverter implements AttributeConverter<Pizza, String> {

    @Override
    public String convertToDatabaseColumn(Pizza attribute) {
        return attribute.toString();
    }

    @Override
    public Pizza convertToEntityAttribute(String dbData) {
        String[] arr = dbData.split(";");
        String pizzaName = arr[0];
        List<Topping> list = new ArrayList<>(arr.length);
        for(int i = 1; i < arr.length; i++) {
            String[] topping = arr[i].split(" ");
            list.add(new Topping(topping[0], Double.parseDouble(topping[1])));
        }
        return new Pizza(pizzaName, list);
    }
}
