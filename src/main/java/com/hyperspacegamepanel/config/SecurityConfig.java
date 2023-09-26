package com.hyperspacegamepanel.config;

import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.repositories.UserRepository;
import com.hyperspacegamepanel.utils.Alert;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityConfig {

    @Autowired private UserRepository userRepo;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsServiceImpl userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService());
        daoAuthenticationProvider.setPostAuthenticationChecks(toCheck -> {
            if(!((CustomUserDetails) toCheck).isVerified()) {
                throw new DisabledException("USER_IS_NOT_VERIFIED");
            }
        });
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/panel/**").hasRole("USER")
                                .requestMatchers("/**").permitAll()
                )
                .formLogin(formLogin ->
                        formLogin
                                .successHandler(this::handleSuccessfulLogin)
                                .failureHandler(this::handleFailedLogin)
                                .loginPage("/login")
                )
                .logout(logout ->
                        logout.logoutSuccessHandler(this::handleLogoutSuccess)
                )
                .build();
    }

    private void handleSuccessfulLogin(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        request.getSession().setAttribute("status", new Alert("Login successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
        User user = userRepo.getByEmail(authentication.getName());
        String redirectURL = "";

        if (user.getRole().equals(User.ROLE_ADMIN)) {
            redirectURL = "/admin/dashboard";
        }
        if (user.getRole().equals(User.ROLE_USER)) {
            redirectURL = "/me/dashboard";
        }

        response.sendRedirect(redirectURL);
    }

    private void handleFailedLogin(HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException {
        if (exception instanceof DisabledException && exception.getMessage().equals("USER_IS_NOT_VERIFIED")) {
            request.getSession().setAttribute("status", new Alert("Your account is not verified, please verify to login.", Alert.ERROR, Alert.ERROR_CLASS));
            response.sendRedirect("/verifyAccount?");
        } else if (exception.getClass().isAssignableFrom(DisabledException.class)) {
            request.getSession().setAttribute("status", new Alert("Your Account is suspended, please contact admin to unblock.", Alert.ERROR, Alert.ERROR_CLASS));
            response.sendRedirect("/login?=AccountSuspended");
        } else if (exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
            request.getSession().setAttribute("status", new Alert("The username, email, or password you entered is incorrect.", Alert.ERROR, Alert.ERROR_CLASS));
            response.sendRedirect("/login?=BadCredentials");
        }
    }

    private void handleLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication != null) {
            request.getSession().setAttribute("status", new Alert("Logout successfully", Alert.SUCCESS, Alert.SUCCESS_CLASS));
            response.sendRedirect("/login?s=logout");
        } else {
            response.sendRedirect("/login?e");
        }
    }


}
