package nl.tudelft.sem.template.example.controllers;

import lombok.Data;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.Coupon;
import nl.tudelft.sem.template.example.domain.CouponRepository;
import nl.tudelft.sem.template.example.domain.CouponType;
import nl.tudelft.sem.template.example.domain.DiscountCouponIncompleteException;
import nl.tudelft.sem.template.example.domain.InvalidCouponCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Coupon Controller.
 */
@RestController
@Data
public class CouponController {

    private final transient AuthManager authManager;

    private final CouponRepository repo;

    @Autowired
    public CouponController(AuthManager authManager, CouponRepository repo) {
        this.authManager = authManager;
        this.repo = repo;
    }

    /**
     * Retrieves coupon using passed code.
     * throws InvalidCouponException if the coupon code format is incorrect.
     *
     * @param code the coupon code provided
     * @return Coupon with given code if exists
     */
    @GetMapping("/coupon")
    public ResponseEntity<Coupon> getCouponByCode(@RequestBody String code) {
        if (!Coupon.validCodeFormat(code)) {
            throw new InvalidCouponCodeException(code);
        }
        if (!repo.existsById(code)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(code).get());
    }

    /**
     * Adds a new coupon from the database, storeId depends on person calling (regional manager or store owner).
     *
     * @param coupon the coupon code provided
     * @return Coupon with given code if exists
     * @throws DiscountCouponIncompleteException if discount percentage is missing for a new discount coupon
     */
    @PostMapping("/addCoupon")
    public ResponseEntity<Coupon> addCoupon(@RequestBody Coupon coupon) throws DiscountCouponIncompleteException {
        if (!Coupon.validCodeFormat(coupon.getCode())) {
            throw new InvalidCouponCodeException(coupon.getCode());
        }
        if (coupon.getType().equals(CouponType.DISCOUNT) && Integer.valueOf(coupon.getPercentage()) == null) {
            throw new DiscountCouponIncompleteException();
        }
        try {
            //TODO: Check whether it is by regional manager or store owner
            boolean isRegionalManager = true; //TODO: update this
            if (isRegionalManager) {
                coupon.setStoreId(0L);
            } else {
                long ownedStoreId = 1L; //TODO: insert caller's storeId
                coupon.setStoreId(ownedStoreId);
            }
            repo.save(coupon);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

}
