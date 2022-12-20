package nl.tudelft.sem.checkout.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.AttributeConverter;

public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        return String.valueOf(attribute.getYear()) + '-'
            + attribute.getMonth() + '-'
            + attribute.getDayOfMonth() + ' '
            + attribute.getHour() + ':'
            + attribute.getMinute();
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        return LocalDateTime.of(Integer.parseInt(dbData.substring(0, 4)),
            Integer.parseInt(dbData.substring(5, 7)),
            Integer.parseInt(dbData.substring(8, 10)),
            Integer.parseInt(dbData.substring(11, 13)),
            Integer.parseInt(dbData.substring(14)));
    }
}
