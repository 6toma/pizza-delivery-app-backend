package nl.tudelft.sem.store;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * This basically disables any security, only used for testing.
     **/
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable() // disable csrf because we do not use cookies in the website
                .authorizeRequests().antMatchers("/").permitAll();
    }

}

