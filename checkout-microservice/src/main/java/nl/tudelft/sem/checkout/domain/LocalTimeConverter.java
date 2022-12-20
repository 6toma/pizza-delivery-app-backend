package nl.tudelft.sem.checkout.domain;

import javax.persistence.AttributeConverter;
import java.time.LocalTime;

public class LocalTimeConverter implements AttributeConverter<LocalTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalTime attribute) {
        return String.valueOf(attribute.getHour()) +
                ':' +
                attribute.getMinute();
    }

    @Override
    public LocalTime convertToEntityAttribute(String dbData) {
        return LocalTime.of(Integer.parseInt(dbData.substring(0, 2)), Integer.parseInt(dbData.substring(3, 5)));
    }
}
