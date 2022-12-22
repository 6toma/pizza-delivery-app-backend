package nl.tudelft.sem.template.commons.models;

import lombok.Data;

@Data
public class CouponFinalPriceModel implements Comparable<CouponFinalPriceModel> {

    private String code;
    private double price;

    public CouponFinalPriceModel(String code, double price) {
        this.code = code;
        this.price = price;
    }

    @Override
    public int compareTo(CouponFinalPriceModel o) {
        return Double.compare(this.price, o.price);
    }
}
