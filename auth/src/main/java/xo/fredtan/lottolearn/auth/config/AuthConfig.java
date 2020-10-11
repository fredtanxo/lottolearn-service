package xo.fredtan.lottolearn.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.client.RestTemplate;
import xo.fredtan.lottolearn.api.auth.constants.AuthConstants;
import xo.fredtan.lottolearn.auth.converter.OAuth2AccessTokenResponseConverter;
import xo.fredtan.lottolearn.auth.converter.OAuth2UserInfoRequestConverter;
import xo.fredtan.lottolearn.auth.filter.JwtAuthenticationFilter;
import xo.fredtan.lottolearn.auth.filter.JwtLogoutFilter;
import xo.fredtan.lottolearn.auth.filter.JwtRefreshFilter;
import xo.fredtan.lottolearn.auth.service.UserDetailsServiceImpl;
import xo.fredtan.lottolearn.common.constant.LotToLearnConstants;

import java.util.Arrays;

@EnableWebSecurity
public class AuthConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
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
                    .antMatchers(HttpMethod.POST, AuthConstants.LOGIN_URL, AuthConstants.REFRESH_URL).permitAll()
                    .antMatchers(HttpMethod.DELETE, AuthConstants.LOGOUT_URL).permitAll()
                    .antMatchers(HttpMethod.GET, AuthConstants.JWK_SET_URL).permitAll()
                    .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 ->
                oauth2
                    .successHandler(thirdPartyAuthenticationSuccessHandler())
                    .failureHandler(thirdPartyAuthenticationFailureHandler())
                    .tokenEndpoint(token ->
                        token
                            .accessTokenResponseClient(authorizationCodeTokenResponseClient())
                    )
                    .userInfoEndpoint(userInfo ->
                        userInfo
                            .userService(userInfoService())
                    )
            )
            .addFilterAfter(new JwtLogoutFilter(authenticationManager()), OAuth2LoginAuthenticationFilter.class)
            .addFilterAfter(new JwtRefreshFilter(authenticationManager()), JwtLogoutFilter.class)
            .addFilterAfter(new JwtAuthenticationFilter(authenticationManager()), JwtRefreshFilter.class)
            .sessionManagement(sessionManagement ->
                sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 第三方登录跳转需要Session保持Authentication
            );
    }

    /**
     * 部分平台在获取{@code access_token}时会缺少/附加某些属性
     * 如：微博返回{@code access_token}时缺少{@code token_type}，附加{@code uid}、{@code isRealName}等
     * @return the {@link OAuth2AccessTokenResponseClient}
     */
    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient() {
        OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter =
                new OAuth2AccessTokenResponseHttpMessageConverter();
        tokenResponseHttpMessageConverter.setTokenResponseConverter(new OAuth2AccessTokenResponseConverter());

        RestTemplate restTemplate = new RestTemplate(
                Arrays.asList(new FormHttpMessageConverter(), tokenResponseHttpMessageConverter)
        );
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

        DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        tokenResponseClient.setRestOperations(restTemplate);

        return tokenResponseClient;
    }

    /**
     * 部分平台提供的{@code access_token}不是{@code Bearer}类型，调用API时是作为URL参数传递
     * 微博还要求提供{@code uid}
     * @return the {@link OAuth2UserService}
     */
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> userInfoService() {
        DefaultOAuth2UserService userService = new DefaultOAuth2UserService();
        userService.setRequestEntityConverter(new OAuth2UserInfoRequestConverter());
        return userService;
    }
}
