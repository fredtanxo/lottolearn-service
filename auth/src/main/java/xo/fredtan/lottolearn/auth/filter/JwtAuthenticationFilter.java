package xo.fredtan.lottolearn.auth.filter;

import com.alibaba.fastjson.JSON;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import xo.fredtan.lottolearn.api.auth.constant.AuthConstants;
import xo.fredtan.lottolearn.auth.domain.JwtUser;
import xo.fredtan.lottolearn.auth.util.JwtUtil;
import xo.fredtan.lottolearn.domain.auth.request.FormLoginRequest;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final RSAKey rsaKey;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, RSAKey rsaKey) {
        super.setAuthenticationManager(authenticationManager);
        this.rsaKey = rsaKey;
        super.setFilterProcessesUrl(AuthConstants.LOGIN_URL);
    }

    /**
     * 用户名密码登录，获取完整用户信息
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Authentication
     * @throws AuthenticationException AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            FormLoginRequest formLoginRequest = JSON.parseObject(
                    request.getInputStream(), StandardCharsets.UTF_8, FormLoginRequest.class);
            if (Objects.isNull(formLoginRequest)) {
                return null;
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    formLoginRequest.getUsername(), formLoginRequest.getPassword());
            setDetails(request, authenticationToken);
            return this.getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 登录信息校验成功后生成JWT令牌
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param chain FilterChain
     * @param authResult Authentication
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        JwtUser principal = (JwtUser) authResult.getPrincipal();
        List<String> authorities = principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, String> claims = Map.of(AuthConstants.TOKEN_CLAIM_KEY, String.join(" ", authorities),
                "nickname", principal.getNickname());

        String jwt = JwtUtil.issueRSAToken(rsaKey, AuthConstants.ISSUER, principal.getUserId(),
                claims,
                AuthConstants.EXPIRATION_OFFSET);

        response.setHeader(AuthConstants.TOKEN_RESPONSE_HEADER, AuthConstants.TOKEN_RESPONSE_PREFIX + jwt);
    }

    /**
     * 登录信息校验失败发送401状态码
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param failed AuthenticationException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
