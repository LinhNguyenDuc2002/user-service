package com.example.userservice.config;

import com.example.userservice.security.resourceserver.JwtResourceServerCustomize;
import com.example.userservice.security.resourceserver.TokenResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true) //allow use @PreAuthorize, @Secured
public class WebSecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtResourceServerCustomize jwtResourceServerCustomize;

    @Autowired
    private TokenResolver tokenResolver;

    /**
     * Authenticate user when login
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable()) //disable CORS
                .csrf(csrf -> csrf.disable()) //disable CSRF
                .httpBasic(httpBasic -> httpBasic.disable()) //disable HTTP Basic Authentication
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtResourceServerCustomize)
                        .bearerTokenResolver(tokenResolver) //handle and authenticate jwt received from request
                ) //config Resource server
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new BasicAuthenticationEntryPoint()) //handle authentication exception
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()) //handle access denied exception
                )
                .authorizeRequests() //config authentication rules for requests
                .requestMatchers("/api/auth", "/api/users/verify").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().permitAll()
                .and()
                .oauth2Login() //allow user to login by using Oauth2
                .defaultSuccessUrl("/user/oauth", true); //redirect url after authenticated successfully

        //filter through jwtAuthenticationFilter() before UsernamePasswordAuthenticationFilter
//        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
