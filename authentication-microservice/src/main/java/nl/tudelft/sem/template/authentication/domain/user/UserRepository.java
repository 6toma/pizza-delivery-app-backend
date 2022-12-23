package nl.tudelft.sem.template.authentication.domain.user;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.authentication.NetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for querying and persisting user aggregate roots.
 */
@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {
    /**
     * Find user by NetID.
     */
    Optional<AppUser> findByNetId(NetId netId);

    /**
     * Check if an existing user already uses a NetID.
     */
    boolean existsByNetId(NetId netId);

    /**
     * Finds all roles with a specific role.
     *
     * @param role The role to search
     * @return List of users with the role
     */
    List<AppUser> findAllByRole(UserRole role);

}
