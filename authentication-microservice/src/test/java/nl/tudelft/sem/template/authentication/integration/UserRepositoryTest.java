package nl.tudelft.sem.template.authentication.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import nl.tudelft.sem.template.authentication.NetId;
import nl.tudelft.sem.template.authentication.domain.user.AppUser;
import nl.tudelft.sem.template.authentication.domain.user.HashedPassword;
import nl.tudelft.sem.template.authentication.domain.user.UserRepository;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    UserRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void findByNetIdFound() {
        var user1 = repository.save(user("user1", UserRole.CUSTOMER));
        repository.save(user("user2", UserRole.CUSTOMER));
        var found = repository.findByNetId(new NetId("user1"));
        assertTrue(found.isPresent());
        assertEquals(user1, found.get());
    }

    @Test
    void findByNetIdNotFound() {
        repository.save(user("user1", UserRole.CUSTOMER));
        var found = repository.findByNetId(new NetId("user2"));
        assertFalse(found.isPresent());
    }

    @Test
    void existsByIdTrue() {
        repository.save(user("user1", UserRole.CUSTOMER));
        assertTrue(repository.existsByNetId(new NetId("user1")));
    }

    @Test
    void existsByIdFalse() {
        repository.save(user("user1", UserRole.CUSTOMER));
        assertFalse(repository.existsByNetId(new NetId("user2")));
    }

    @Test
    void findAllByRoleOneMatch() {
        repository.save(user("user1", UserRole.CUSTOMER));
        var user2 = repository.save(user("user2", UserRole.STORE_OWNER));
        repository.save(user("user3", UserRole.REGIONAL_MANAGER));
        var result = repository.findAllByRole(UserRole.STORE_OWNER);
        assertEquals(Collections.singletonList(user2), result);
    }

    @Test
    void findAllByRoleMultipleMatches() {
        repository.save(user("user1", UserRole.CUSTOMER));
        repository.save(user("user2", UserRole.STORE_OWNER));
        var user3 = repository.save(user("user3", UserRole.REGIONAL_MANAGER));
        var user4 = repository.save(user("user4", UserRole.REGIONAL_MANAGER));
        var result = repository.findAllByRole(UserRole.REGIONAL_MANAGER);
        assertEquals(Arrays.asList(user3, user4), result);

    }

    @Test
    void findAllByRoleNoMatches() {
        repository.save(user("user1", UserRole.CUSTOMER));
        repository.save(user("user2", UserRole.STORE_OWNER));
        var result = repository.findAllByRole(UserRole.REGIONAL_MANAGER);
        assertTrue(result.isEmpty());
    }

    private AppUser user(String netId, UserRole role) {
        return new AppUser(new NetId(netId), role, new HashedPassword("HashedPassword"));
    }

}