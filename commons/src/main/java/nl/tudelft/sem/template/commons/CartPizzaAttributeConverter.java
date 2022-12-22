package nl.tudelft.sem.template.commons;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeConverter;
import nl.tudelft.sem.template.commons.entity.CustomPizza;
import nl.tudelft.sem.template.commons.entity.Topping;
import nl.tudelft.sem.template.commons.models.CartPizza;

public class CartPizzaAttributeConverter implements AttributeConverter<CartPizza, String> {

    @Override
    public String convertToDatabaseColumn(CartPizza attribute) {
        return attribute.toString();
    }

    @Override
    public CartPizza convertToEntityAttribute(String dbData) {
        String[] pizzaAndAmount = dbData.split("-");
        String[] arr = pizzaAndAmount[0].split(";");
        String pizzaName = arr[0];
        List<Topping> list = new ArrayList<>(arr.length);
        for(int i = 1; i < arr.length - 1; i++) {
            String[] topping = arr[i].split(" ");
            list.add(new Topping(topping[0], Double.parseDouble(topping[1])));
        }
        String price = arr[arr.length - 1];
        CustomPizza pizza = new CustomPizza(pizzaName, Double.parseDouble(price), list);
        return new CartPizza(pizza, Integer.parseInt(pizzaAndAmount[1]));
    }
}
