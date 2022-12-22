package nl.tudelft.sem.template.checkout.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.AttributeConverter;

public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    private Map<Month, String> monthToNumber = new HashMap<>();

    public LocalDateTimeConverter() {
        monthToNumber.put(Month.JANUARY, "01");
        monthToNumber.put(Month.FEBRUARY, "02");
        monthToNumber.put(Month.MARCH, "03");
        monthToNumber.put(Month.APRIL, "04");
        monthToNumber.put(Month.MAY, "05");
        monthToNumber.put(Month.JUNE, "06");
        monthToNumber.put(Month.JULY, "07");
        monthToNumber.put(Month.AUGUST, "08");
        monthToNumber.put(Month.SEPTEMBER, "09");
        monthToNumber.put(Month.OCTOBER, "10");
        monthToNumber.put(Month.NOVEMBER, "11");
        monthToNumber.put(Month.DECEMBER, "12");
    }

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        StringBuilder builder = new StringBuilder();
        builder.append(attribute.getYear())
            .append('-')
            .append(monthToNumber.get(attribute.getMonth()))
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
