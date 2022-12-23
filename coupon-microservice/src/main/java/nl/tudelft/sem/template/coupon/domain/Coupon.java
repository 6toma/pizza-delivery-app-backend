package nl.tudelft.sem.template.coupon.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
    @JsonDeserialize(using = DateJsonDeserializer.class)
    @JsonSerialize(using = DateJsonSerializer.class)
    private Date expiryDate;

    @Column(name = "storeId", nullable = false)
    private Long storeId;

    @Column(name = "type", nullable = false)
    @Convert(converter = CouponTypeConverter.class)
    private CouponType type;

    @Column(name = "discountPercentage")
    @Min(0)
    @Max(100)
    private Integer percentage;

    public Coupon(String code, Date expiryDate, Long storeId, CouponType type) {
        this.code = code;
        this.expiryDate = expiryDate;
        this.storeId = storeId;
        this.type = type;
    }

    public Coupon(String code, Date expiryDate, Long storeId, CouponType type, Integer percentage) {
        this.code = code;
        this.expiryDate = expiryDate;
        this.storeId = storeId;
        this.type = type;
        this.percentage = percentage;
    }

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
