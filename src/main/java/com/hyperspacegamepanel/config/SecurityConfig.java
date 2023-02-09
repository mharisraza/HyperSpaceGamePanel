package com.hyperspacegamepanel.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.repositories.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepo;

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
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


         http.authorizeHttpRequests()
        .antMatchers("/admin/**")
        .hasRole("ADMIN")
        .antMatchers("/order/**")
        .hasRole("NORMAL")
        .antMatchers("/me/**")
        .hasRole("NORMAL")
        .antMatchers("/**")
        .permitAll()
        .and()
        .formLogin()
        .successHandler(new AuthenticationSuccessHandler() {
                        // do actions if user login successfully.
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {

                        request.getSession().setAttribute("status", "LOGIN_SUCCESS");
                        
                        User user = userRepo.getByEmail(authentication.getName());
                        String redirectURL = "";

                        if(user.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
                            redirectURL = "/admin/dashboard";
                        }
                        if(user.getRole().equalsIgnoreCase("ROLE_NORMAL")) {
                            redirectURL = "/me/dashboard";
                        }

                        response.sendRedirect(redirectURL);

            }
        })

        .failureHandler(new AuthenticationFailureHandler() {

            // do actions if user can't login.
            // REASON: user is disabled or bad credentials.
            
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                    AuthenticationException exception) throws IOException, ServletException {
                        
                        if(exception.getClass().isAssignableFrom(DisabledException.class)) {
                            request.getSession().setAttribute("status", "user-disabled");
                            response.sendRedirect("/login?=AccountSuspended");
                            return;
                        }

                        if(exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
                            request.getSession().setAttribute("status", "bad-credentials");
                            response.sendRedirect("/login?=BadCredentials");
                            return;
                        }
            }
        })
        .loginPage("/login")
        .and()
        .logout()
        .logoutSuccessHandler(new LogoutSuccessHandler() {
                     // do actions if user logout successfully
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {
                        if(authentication != null) {
                            request.getSession().setAttribute("status", "logout-success");
                            response.sendRedirect("/login?=logOut");
                        } else {
                            response.sendRedirect("/login?=doLogin");
                        }
            }
            
        })
        .and()
        .csrf()
        .disable();

      
        return http.build();
    }
}
