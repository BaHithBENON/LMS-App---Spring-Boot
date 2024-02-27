package com.lms.library.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.lms.library.services.UserDetailsServiceImpl;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	http
	        .csrf(AbstractHttpConfigurer::disable)
	        .authorizeHttpRequests(request -> request
	        	.requestMatchers("/", "/styles/*", "/login", "/token")
	        	.permitAll()
	        	.anyRequest().authenticated()
	        )
	        .formLogin(form -> form.loginPage("/login")
	                .defaultSuccessUrl("/dashbord", true)
	                .successHandler(new CustomAuthenticationSuccessHandler())
	                .permitAll()
	        )
	    	.addFilterBefore(new JWTAuthenticationFilter(authenticationManager(http), jwtProvider), UsernamePasswordAuthenticationFilter.class)
	        .addFilterAfter(new JWTAuthorizationFilter(jwtProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class)
	        .logout(LogoutConfigurer::permitAll);
    	return http.build();
    }

}
