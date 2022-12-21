package nl.tudelft.sem.template.coupon.domain;

import javax.persistence.Converter;
import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;

@Converter
public class DateConverter implements AttributeConverter<Date, String> {

    @Override
    public String convertToDatabaseColumn(Date date) {
        return date.getDay() + "/" + date.getMonth() + "/" + date.getYear();
    }

    @SneakyThrows
    @Override
    public Date convertToEntityAttribute(String dbData) {
        String[] date = dbData.split("/");
        return new Date(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
    }
}
