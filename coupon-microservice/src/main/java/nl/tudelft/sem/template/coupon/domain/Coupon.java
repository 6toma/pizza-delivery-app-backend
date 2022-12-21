package nl.tudelft.sem.template.coupon.domain;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Entity
@Table(name = "coupons")
@NoArgsConstructor
@Getter
@Setter
public class Coupon {

    @Id
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "expiryDate", nullable = false)
    @Convert(converter = DateConverter.class)
    private Date expiryDate;

    @Column(name = "storeId", nullable = false)
    private Long storeId;

    @Column(name = "type", nullable = false)
    @Convert(converter = CouponTypeConverter.class)
    private CouponType type;

    @Column(name = "discountPercentage")
    private Integer percentage;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coupon coupon = (Coupon) o;
        return storeId == coupon.storeId && Objects.equals(code, coupon.code)
            && Objects.equals(expiryDate, coupon.expiryDate)
            && Objects.equals(type, coupon.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, expiryDate, storeId, type);
    }

    @Override
    public String toString() {
        return "Coupon{" +
            "code='" + code + '\'' +
            ", expiryDate=" + expiryDate +
            ", storeId=" + storeId +
            ", type=" + type +
            ", percentage=" + percentage +
            '}';
    }


}
