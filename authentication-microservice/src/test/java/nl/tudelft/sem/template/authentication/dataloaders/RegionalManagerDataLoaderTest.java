package nl.tudelft.sem.template.authentication.dataloaders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import nl.tudelft.sem.template.authentication.domain.user.AppUser;
import nl.tudelft.sem.template.authentication.domain.user.HashedPassword;
import nl.tudelft.sem.template.authentication.domain.user.NetId;
import nl.tudelft.sem.template.authentication.domain.user.PasswordHashingService;
import nl.tudelft.sem.template.authentication.domain.user.UserRepository;
import nl.tudelft.sem.template.authentication.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;

class RegionalManagerDataLoaderTest {

    private ApplicationArguments applicationArguments;
    private PasswordHashingService passwordHashingService;
    private UserRepository userRepository;
    private RegionalManagerDataLoader dataLoader;

    @BeforeEach
    void setup() {
        applicationArguments = mock(ApplicationArguments.class);
        passwordHashingService = mock(PasswordHashingService.class);
        userRepository = mock(UserRepository.class);
        dataLoader = new RegionalManagerDataLoader(userRepository, passwordHashingService);
    }

    @Test
    void testRunNoRegionalManagers() {
        when(userRepository.findAllByRole(UserRole.REGIONAL_MANAGER)).thenReturn(Collections.emptyList());
        when(passwordHashingService.hash(any())).thenReturn(new HashedPassword("hashed"));
        dataLoader.run(applicationArguments);
        verify(userRepository, times(1)).findAllByRole(any());
        verify(passwordHashingService, times(1)).hash(any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testRunHasRegionalManager() {
        var user = new AppUser(new NetId("user"), UserRole.REGIONAL_MANAGER, new HashedPassword("HashedPassword"));
        when(userRepository.findAllByRole(UserRole.REGIONAL_MANAGER)).thenReturn(Collections.singletonList(user));
        dataLoader.run(applicationArguments);
        verify(userRepository, times(1)).findAllByRole(any());
        verify(passwordHashingService, times(0)).hash(any());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void testRunNoRegionalManagersPasswordNull() {
        when(userRepository.findAllByRole(UserRole.REGIONAL_MANAGER)).thenReturn(Collections.emptyList());
        dataLoader.run(applicationArguments);
        verify(userRepository, times(1)).findAllByRole(any());
        verify(passwordHashingService, times(1)).hash(any());
        verify(userRepository, times(0)).save(any());
    }

}