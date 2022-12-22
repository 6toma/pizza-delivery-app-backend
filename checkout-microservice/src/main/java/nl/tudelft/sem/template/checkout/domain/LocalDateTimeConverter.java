package nl.tudelft.sem.template.checkout.domain;

import java.time.LocalDateTime;
import javax.persistence.AttributeConverter;

public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        StringBuilder builder = new StringBuilder();
        builder.append(attribute.getYear())
            .append('-')
            .append(String.format("%02d", attribute.getMonth().getValue()))
            .append('-')
            .append(attribute.getDayOfMonth())
            .append(' ')
            .append(attribute.getHour())
            .append(':');
        int minutes = attribute.getMinute();
        if(minutes < 10)
            builder.append('0');
        builder.append(minutes);
        return builder.toString();
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
