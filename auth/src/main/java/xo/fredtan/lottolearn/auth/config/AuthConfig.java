package xo.fredtan.lottolearn.auth.config;

import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import xo.fredtan.lottolearn.auth.filter.JwtAuthenticationFilter;
import xo.fredtan.lottolearn.auth.service.UserDetailsServiceImpl;

@EnableWebSecurity
public class AuthConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsServiceImpl userDetailsService;
    private final RSAKey rsaKey;

    @Autowired
    public AuthConfig(UserDetailsServiceImpl userDetailsService, RSAKey rsaKey) {
        this.userDetailsService = userDetailsService;
        this.rsaKey = rsaKey;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                    .antMatchers(HttpMethod.POST, "/auth/login", "/auth/refresh").permitAll()
                    .antMatchers(HttpMethod.DELETE, "/auth/logout").permitAll()
                    .antMatchers(HttpMethod.GET, "/.well-known/jwks.json").permitAll()
                    .anyRequest().authenticated()
            )
            .addFilter(new JwtAuthenticationFilter(authenticationManager(), rsaKey))
            .sessionManagement(sessionManagement ->
                sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
    }
}
