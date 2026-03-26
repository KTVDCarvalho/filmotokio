package filmotokio.security;

import filmotokio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authProvider) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringAntMatchers("/uploads/**", "/api/**")
                )
                .authorizeRequests(auth -> auth
                        .antMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                        .antMatchers("/films/{id}", "/films").permitAll() // Public viewing only
                        .antMatchers("/film/search", "/film/search-results").permitAll() // Public film search
                        .antMatchers("/film/new", "/film/save", "/film/edit/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers("/admin/**", "/films/delete/**").hasRole("ADMIN")
                        .antMatchers("/person/**").authenticated()
                        .antMatchers("/uploads/**").permitAll()
                        // Protect review and score endpoints
                        .antMatchers("/films/*/score", "/films/*/review").authenticated()
                        // API REST endpoints
                        .antMatchers("/api/auth/login").permitAll()
                        .antMatchers("/api/**").authenticated()
                        // Swagger UI endpoints
                        .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authProvider)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint("/login"),
                                new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/**")
                        )
                        .defaultAuthenticationEntryPointFor(
                                jwtAuthenticationEntryPoint,
                                new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/api/**")
                        )
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}