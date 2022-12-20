package nl.tudelft.sem.template.coupon.controllers;

import java.util.PriorityQueue;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.annotations.role.RoleStoreOwnerOrRegionalManager;
import nl.tudelft.sem.template.commons.utils.RequestHelper;
import nl.tudelft.sem.template.coupon.domain.Coupon;
import nl.tudelft.sem.template.coupon.domain.CouponRepository;
import nl.tudelft.sem.template.coupon.domain.CouponType;
import nl.tudelft.sem.template.coupon.domain.DiscountCouponIncompleteException;
import nl.tudelft.sem.template.coupon.domain.InvalidCouponCodeException;
import nl.tudelft.sem.template.coupon.domain.NotRegionalManagerException;
import nl.tudelft.sem.template.coupon.services.CouponService;
import nl.tudelft.sem.template.coupon.services.Tuple;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Coupon Controller.
 */
@RestController
@RequiredArgsConstructor
@Data
public class CouponController {

    private final transient AuthManager authManager;
    private final RequestHelper requestHelper;
    private final CouponRepository repo;

    /**
     * Retrieves coupon using passed code.
     * throws InvalidCouponException if the coupon code format is incorrect.
     *
     * @param code the coupon code provided
     * @return Coupon with given code if exists
     */
    @GetMapping("/coupon")
    public ResponseEntity<Coupon> getCouponByCode(@RequestBody String code) {
        if (!CouponService.validCodeFormat(code)) {
            throw new InvalidCouponCodeException(code);
        }
        if (!repo.existsById(code)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(code).get());
    }

    @GetMapping("/getCouponsForStore")
    public ResponseEntity<List<Coupon>> getCouponsForStore() {
        return ResponseEntity.ok(repo.findByStoreId(getStoreId()));
    }

    private long getStoreId() {
        return 1L; //TODO: change this to request
    }

    /**
     * Adds a new coupon from the database, storeId depends on person calling (regional manager or store owner).
     *
     * @param coupon the coupon code provided
     * @return Coupon with given code if exists
     * @throws DiscountCouponIncompleteException if discount percentage is missing for a new discount coupon
     */
    @RoleStoreOwnerOrRegionalManager
    @PostMapping("/addCoupon")
    public ResponseEntity<Coupon> addCoupon(@RequestBody long storeId, @RequestBody Coupon coupon) throws DiscountCouponIncompleteException {
        if (coupon.getCode() == null) {
            throw new InvalidCouponCodeException("No coupon code provided!");
        }
        if (!CouponService.validCodeFormat(coupon.getCode())) {
            throw new InvalidCouponCodeException(coupon.getCode());
        }
        if (coupon.getPercentage() == null && coupon.getType() == CouponType.DISCOUNT) {
            throw new DiscountCouponIncompleteException();
        }
        if(storeId == -1 && !authorRole("ROLE_REGIONAL_MANAGER"))
            throw new NotRegionalManagerException();
        try {
            String netId = authManager.getNetId();
            if (true) { //TODO: call endpoint from store to verify storeId
                coupon.setStoreId(storeId);
                repo.save(coupon);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/selectCoupon")
    public ResponseEntity<Tuple> selectCoupon(@RequestBody List<Double> prices, @RequestBody List<String> codes) {
        PriorityQueue<Tuple> pq = new PriorityQueue<>();
        for (String code : codes) {
            ResponseEntity<Coupon> c;
            ResponseEntity<Boolean> used = null; //TODO: endpoint needs to be created in customer
            try {
                c = getCouponByCode(code);
                //TODO: call endpoint for used
            } catch (InvalidCouponCodeException e) {
                continue;
            }
            if(c.getStatusCode().equals(HttpStatus.BAD_REQUEST) || used.getBody())
                continue;
            Coupon coupon = c.getBody();
            if (coupon.getType() == CouponType.DISCOUNT) {
                pq.add(new Tuple(code, CouponService.applyDiscount(coupon, prices)));
            } else {
                pq.add(new Tuple(code, CouponService.applyOnePlusOne(coupon, prices)));
            }
        }
        if(pq.isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(pq.peek());
    }

    public String getRoles() {
        return SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList())
            .get(0);
    }

    public boolean authorRole(String role) {
        return getRoles().equals(role);
    }

}
