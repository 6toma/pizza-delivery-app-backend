package nl.tudelft.sem.template.authentication.annotations.role;


import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole(T(nl.tudelft.sem.template.authentication.domain.user.UserRole).STORE_OWNER.jwtRoleName)")
public @interface RoleStoreOwner {
}
