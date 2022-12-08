package nl.tudelft.sem.template.authentication.config;

import lombok.RequiredArgsConstructor;
import nl.tudelft.sem.template.authentication.JwtAuthenticationEntryPoint;
import nl.tudelft.sem.template.authentication.JwtRequestFilter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring boot configuration for integration testing in this module. Since the module doesn't contain a spring boot
 * application, we require this configuration for the integration tests to work.
 */
@SpringBootConfiguration
@ComponentScan("nl.tudelft.sem.template.authentication")
@EnableGlobalMethodSecurity(prePostEnabled = true) // Needed to enable the @PreAuthorize annotation
@RequiredArgsConstructor
public class TestServerConfig extends WebSecurityConfigurerAdapter {

    private final transient JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final transient JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
