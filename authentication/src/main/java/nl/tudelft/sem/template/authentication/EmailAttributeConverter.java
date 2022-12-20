package nl.tudelft.sem.template.authentication;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the email value object.
 */
@Converter
public class EmailAttributeConverter implements AttributeConverter<UserEmail, String> {

    @Override
    public String convertToDatabaseColumn(UserEmail attribute) {
        return attribute.toString();
    }

    @Override
    public UserEmail convertToEntityAttribute(String dbData) {
        return new UserEmail(dbData);
    }

}

