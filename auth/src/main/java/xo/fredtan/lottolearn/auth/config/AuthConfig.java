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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import xo.fredtan.lottolearn.auth.filter.JwtAuthenticationFilter;
import xo.fredtan.lottolearn.auth.service.UserDetailsServiceImpl;
import xo.fredtan.lottolearn.common.constant.LotToLearnConstants;

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

    /**
     * 第三方OAuth2登录成功的handler
     * 跳转到{@link /login/third-party}进行授权跳转
     */
    private SavedRequestAwareAuthenticationSuccessHandler thirdPartyAuthenticationSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/login/third-party");
        return handler;
    }

    /**
     * 登录失败跳转回首页
     */
    private AuthenticationFailureHandler thirdPartyAuthenticationFailureHandler() {
        return (request, response, exception) -> response.sendRedirect(
                String.format("%s?failure=true", LotToLearnConstants.LOGIN_PAGE)
        );
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
            .oauth2Login(oauth2 ->
                oauth2
                    .successHandler(thirdPartyAuthenticationSuccessHandler())
                    .failureHandler(thirdPartyAuthenticationFailureHandler())
            )
            .addFilter(new JwtAuthenticationFilter(authenticationManager(), rsaKey))
            .sessionManagement(sessionManagement ->
                sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 第三方登录跳转需要Session保持Authentication
            );
    }
}
