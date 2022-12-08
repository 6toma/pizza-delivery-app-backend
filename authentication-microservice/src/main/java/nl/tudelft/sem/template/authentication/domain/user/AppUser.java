package nl.tudelft.sem.template.authentication.domain.user;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nl.tudelft.sem.template.authentication.domain.HasEvents;

/**
 * A DDD entity representing an application user in our domain.
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class AppUser extends HasEvents {
    /**
     * Identifier for the application user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    @Setter
    private int id;

    @Column(name = "net_id", nullable = false, unique = true)
    @Convert(converter = NetIdAttributeConverter.class)
    private NetId netId;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    private UserRole role;

    @Column(name = "password_hash", nullable = false)
    @Convert(converter = HashedPasswordAttributeConverter.class)
    private HashedPassword password;

    /**
     * Create new application user. By default, their role will be set to customer.
     *
     * @param netId    The NetId for the new user
     * @param password The password for the new user
     */
    public AppUser(NetId netId, HashedPassword password) {
        this.netId = netId;
        this.role = UserRole.CUSTOMER;
        this.password = password;
        this.recordThat(new UserWasCreatedEvent(netId));
    }

    /**
     * Create new application user with specific role.
     *
     * @param netId    The NetId for the new user
     * @param role     The role of the new user
     * @param password The password for the new user
     */
    public AppUser(NetId netId, UserRole role, HashedPassword password) {
        this.netId = netId;
        this.role = role;
        this.password = password;
        this.recordThat(new UserWasCreatedEvent(netId));
    }

    /**
     * Changes the password of the user and emits a {@link PasswordWasChangedEvent} event.
     *
     * @param password The new password of the user
     */
    public void changePassword(HashedPassword password) {
        this.password = password;
        this.recordThat(new PasswordWasChangedEvent(this));
    }

    public NetId getNetId() {
        return netId;
    }

    public HashedPassword getPassword() {
        return password;
    }
}
