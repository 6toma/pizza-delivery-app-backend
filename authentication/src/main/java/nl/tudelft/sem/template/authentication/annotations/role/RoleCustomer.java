package nl.tudelft.sem.template.authentication.annotations.role;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority(T(nl.tudelft.sem.template.authentication.domain.user.UserRole).CUSTOMER.jwtRoleName)")
public @interface RoleCustomer {
}
