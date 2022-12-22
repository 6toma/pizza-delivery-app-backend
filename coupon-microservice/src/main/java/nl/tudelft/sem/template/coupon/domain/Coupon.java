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

@Entity
@Table(name = "coupons")
@NoArgsConstructor
@Getter
@Setter
public class Coupon {

    @Bean
    public Clock clockBean() {
        return Clock.systemDefaultZone();
    }

    @Autowired
    private Clock clock;

    @Id
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "expireDate", nullable = false)
    @Convert(converter = DateConverter.class)
    private Date expiryDate;

    @Column(name = "storeId")
    private long storeId;

    @Column(name = "type")
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
        return "Coupon{"
            + "code='" + code + '\''
            + ", expiryDate=" + expiryDate
            + ", storeId=" + storeId
            + ", type=" + type
            + ", percentage=" + percentage
            + '}';
    }

    /**
     * Method isValid checks whether a coupon is eligible. A coupon is eligible if the expiry date has not yet been met.
     *
     * @return boolean
     */
    public boolean isValid() {
        LocalDate currDate = LocalDate.now(clock);
        LocalDate ed = LocalDate.of(expiryDate.getYear(), expiryDate.getMonth(), expiryDate.getDay());
        return currDate.isBefore(ed);
    }

    /**
     * Checks if the code format is valid. A code is valid if it is composed by 4 letters followed by 2 digits.
     *
     * @return boolean
     */
    public static boolean validCodeFormat(String code) {
        if (code == null) {
            return false;
        }
        return code.length() == 6 && code.substring(0, 4).chars().allMatch(Character::isLetter)
            && code.substring(4).chars().allMatch(Character::isDigit);
    }
}
