package com.easybytes.config;

import com.easybytes.filter.CsrfCookieFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration // during the startup spring will scan for all the beans that we have defined in this class
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        // CSRF TOKEN HANDLER
        // with this Spring Security is generating the CSRF token for you.
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        // even if we don't mention this line, be default the handler is going to construct the same name
        requestHandler.setCsrfRequestAttributeName("_csrf");


        http
                // SESSION MANAGEMENT
                // we now have an UI app with a login page and don't need the default spring security login page
                // this tells Spring Security to create the JSESSIONID
                // by following this session management created here
                // and after the initial login is completed
                // the JSESSIONID will be sent to the UI and be used for the subsequent requests
                // without these 2 lines you have to share the credentials
                // every time you are trying to access the secured APIs form the Angular app
                .securityContext(context -> context.requireExplicitSave(false))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))

                // CORS
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                    configuration.setAllowedMethods(List.of("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(List.of("*"));
                    // telling that the browser can remember these configurations up to 1h,
                    // it is going to cache these details up to 1h
                    // usually, in prod, we can set this for 24h or 30d, based on the prod deployment cycle.
                    configuration.setMaxAge(3600L);
                    return configuration;
                }))

                // CSRF
                // These are public APIs and we don't need to handle CSRF attacks because there is no sensitive information
                // we don't need to add /notices here because it is a GET request
                // with .httpOnlyFalse() we are telling to the Spring security
                // to create a CSRF cookie with a configuration as HttpOnlyFalse
                // so that my JavaScript code deployed inside the angular application can read the cookie value.
                .csrf(csrf -> csrf.csrfTokenRequestHandler(requestHandler)
                        .ignoringRequestMatchers("/contact", "/register")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                // execute CsrfCookieFilter after the BasicAuthenticationFilter
                // only after the BasicAuthenticationFilter the login operation will complete
                // and the csrf token will be generated
                // and the crsf token will be persisted in the response with the help of CsrfCookieFilter
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)

                // AUTHORIZATION
                .authorizeHttpRequests(requests -> requests
//                        .requestMatchers("/myAccount").hasAuthority("VIEWACCOUNT")
//                        .requestMatchers("/myBalance").hasAnyAuthority("VIEWACCOUNT", "VIEWBALANCE")
//                        .requestMatchers("/myLoans").hasAuthority("VIEWLOANS")
//                        .requestMatchers("/myCards").hasAuthority("VIEWCARDDETAILS")
                        .requestMatchers("/myAccount").hasRole("USER")
                        .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/myLoans").hasRole("USER")
                        .requestMatchers("/myCards").hasRole("USER")
                        .requestMatchers("/user").authenticated()
                        .requestMatchers("/notices", "/contact", "/register").permitAll())
                .formLogin(withDefaults())
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
