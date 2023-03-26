package ru.shvets.worldbank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.shvets.worldbank.model.Permission;
import ru.shvets.worldbank.security.LoginFilter;

import javax.servlet.http.Cookie;


@Configuration
@EnableWebSecurity
public class SpringConfig {
    private final UserDetailsService userDetailsService;

    @Autowired
    public SpringConfig(@Qualifier("userDetailsServiceImp") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AbstractAuthenticationProcessingFilter loginFilter =
                new LoginFilter();
        loginFilter.setAuthenticationManager(authenticationManager(http));

        Cookie sessionCookie = new Cookie("JSESSIONID", null);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(0);
        CookieClearingLogoutHandler logoutHandler = new CookieClearingLogoutHandler(sessionCookie);

        http
                .csrf().disable()
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/api/v1/auth/login").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/**").hasAuthority(Permission.READ.getPermission())
                .antMatchers(HttpMethod.POST, "/api/v1/**").hasAuthority(Permission.WRITE.getPermission())
                .antMatchers(HttpMethod.PATCH, "/api/v1/**").hasAuthority(Permission.WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, "/api/v1/**").hasAuthority(Permission.WRITE.getPermission())
                .anyRequest()
                .authenticated()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/api/v1/auth/logout", "POST"))
                .addLogoutHandler(logoutHandler);
        ;

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
