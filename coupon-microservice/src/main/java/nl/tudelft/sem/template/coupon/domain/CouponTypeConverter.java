package nl.tudelft.sem.template.example.domain;

import javax.persistence.AttributeConverter;

public class CouponTypeConverter implements AttributeConverter<CouponType, String> {

    @Override
    public String convertToDatabaseColumn(CouponType attribute) {
        return attribute.toString();
    }

    @Override
    public CouponType convertToEntityAttribute(String dbData) {
        return CouponType.valueOf(dbData);
    }

}
