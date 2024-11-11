package com.bapunmalik.voting_system.configs;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.bapunmalik.voting_system.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                    .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()
                    .requestMatchers("/about", "/contact", "/feature").permitAll()
                    .requestMatchers("/admin/**", "/api/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/vote/**").hasRole("USER")
                    .requestMatchers("/dashboard", "/vote").hasRole("USER")
                    .requestMatchers("/users/signup", "/login", "/users/register","/users/register","/pdf/generate").permitAll()
                    .anyRequest().authenticated()
                )
                .formLogin(form -> form
                    .loginProcessingUrl("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .successHandler(new CustomAuthenticationSuccessHandler())
                    .permitAll()
                )
                .logout(logout -> logout
                    .addLogoutHandler(customLogoutHandler())
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
                )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    // Configure the authentication manager to support both in-memory admin and database-backed user login
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("123456"))
            .roles("ADMIN")
            .build();

        return new CompositeUserDetailsService(Arrays.asList(
            new InMemoryUserDetailsManager(admin),
            customUserDetailsService
        ));
    }

     @Bean
    public LogoutHandler customLogoutHandler() {
    return (request, response, authentication) -> {
        SecurityContextHolder.clearContext();  // Clears the authentication context
    
        };
    }
    
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

// Add this helper class in your config package
class CompositeUserDetailsService implements UserDetailsService {
    private final List<UserDetailsService> userDetailsServices;

    public CompositeUserDetailsService(List<UserDetailsService> userDetailsServices) {
        this.userDetailsServices = userDetailsServices;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        for (UserDetailsService service : userDetailsServices) {
            try {
                return service.loadUserByUsername(username);
            } catch (UsernameNotFoundException ignored) {
                // Continue to next service
            }
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}