package nl.tudelft.sem.template.coupon.services;

import lombok.Data;

@Data
public class Tuple implements Comparable<Tuple> {

    private String code;
    private double price;

    public Tuple(String code, double price) {
        this.code = code;
        this.price = price;
    }

    @Override
    public int compareTo(Tuple o) {
        return Double.compare(this.price, o.price);
    }
}
