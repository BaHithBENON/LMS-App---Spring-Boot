package com.lms.library.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.lms.library.services.UserDetailsServiceImpl;


@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class SecurityConfig {
	
	@Autowired 
	CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	
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
    
    @Autowired
	public void configure (AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	http
	        //.csrf(AbstractHttpConfigurer::disable)
    	 	.csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(request -> request
	        	//.requestMatchers("/api/admin/**").hasAuthority("ADMIN")
	        	//.requestMatchers("/api/guest/**").hasAuthority("USER")
	        	.requestMatchers("/admin/**").hasAuthority("ADMIN")
	        	.requestMatchers("/guest/**").hasAuthority("USER")
	        	.requestMatchers(
        			"/", 
        			"/assets/**", 
        			"/utils/**", 
        			"/login",
        			"/tologin",
        			"/token",
        			"/authenticate",
        			"/books/**",
        			"/images/**",
        			"/api/**"
	        	)
	        		.permitAll()
	        	
	        	.anyRequest()
	        		.authenticated()
	        	
	        )
	        .formLogin(form -> form.loginPage("/login")
	                //.defaultSuccessUrl("/api/admin/dashboard", true)
	                .loginProcessingUrl("/login")
	                .successHandler(customAuthenticationSuccessHandler)
	                //.failureUrl("/login?error=true")
	                //.failureForwardUrl("/login?error=true")
	                .permitAll()
	        )
	        //.addFilter(new JWTAuthenticationFilter(authenticationManager(http), jwtProvider))
	    	//.addFilterBefore(new JWTAuthenticationFilter(authenticationManager(http), jwtProvider), UsernamePasswordAuthenticationFilter.class)
	        //.addFilterBefore(new JWTAuthorizationFilter(jwtProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class)
	        //.logout(logout -> logout
            //    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            //    .permitAll()
            //)
	        .logout(form -> form.invalidateHttpSession(true).clearAuthentication(true)
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.logoutSuccessUrl("/login?logout").permitAll())
	        /*
	        .exceptionHandling((exceptionHandling) ->
				exceptionHandling
					.accessDeniedPage("/errors/access-denied")
	        )
	        .sessionManagement((sessionManagement) ->
 				sessionManagement
 					.sessionConcurrency((sessionConcurrency) ->
 						sessionConcurrency
 							.maximumSessions(1)
 							.expiredUrl("/login?expired")
 							.maxSessionsPreventsLogin(true)
 					)
 					.sessionFixation()
 					.newSession()
 					.maximumSessions(1)
 					.expiredUrl("/login?expired")
 					.maxSessionsPreventsLogin(true)
			)
	        */;
    	return http.build();
    }

}
