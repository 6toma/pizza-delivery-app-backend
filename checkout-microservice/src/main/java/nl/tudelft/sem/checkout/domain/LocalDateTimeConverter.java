package nl.tudelft.sem.checkout.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.AttributeConverter;

public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        return String.valueOf(attribute.getYear()) + ':'
            + attribute.getMonth() + ':'
            + attribute.getDayOfMonth() + ':'
            + attribute.getHour() + ':'
            + attribute.getMinute();
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        String[] data = dbData.split(":");
        return LocalDateTime.of(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]));
    }
}
