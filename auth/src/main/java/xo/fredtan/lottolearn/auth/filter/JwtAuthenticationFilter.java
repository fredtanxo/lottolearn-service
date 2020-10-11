package xo.fredtan.lottolearn.auth.filter;

import com.alibaba.fastjson.JSON;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.auth.constants.AuthConstants;
import xo.fredtan.lottolearn.api.user.constants.RoleConstants;
import xo.fredtan.lottolearn.auth.domain.JwtUser;
import xo.fredtan.lottolearn.auth.util.JwkUtils;
import xo.fredtan.lottolearn.auth.util.JwtUtils;
import xo.fredtan.lottolearn.auth.util.TokenResponseUtils;
import xo.fredtan.lottolearn.domain.auth.JwtPair;
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

/**
 * 用户名密码登录的Filter
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
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
            String origin = request.getHeader("origin");
            if (StringUtils.hasText(origin) && !AuthConstants.ORIGIN_LIST.contains(origin)) {
                return null;
            }
            FormLoginRequest formLoginRequest = JSON.parseObject(
                    request.getInputStream(), StandardCharsets.UTF_8, FormLoginRequest.class);
            if (Objects.isNull(formLoginRequest)) {
                return null;
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    formLoginRequest.getUsername(), formLoginRequest.getPassword());
            setDetails(request, authenticationToken);
            return this.getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException ignored) {
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

        String origin = request.getHeader("origin");


        if (AuthConstants.SYSTEM_MANAGEMENT_ORIGIN.equals(origin)) {
            if (authorities.stream().noneMatch(RoleConstants.SYSTEM_ROLES::contains)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        Map<String, String> claims = Map.of(
                AuthConstants.TOKEN_CLAIM_KEY, String.join(" ", authorities),
                "nickname", principal.getNickname()
        );

        JwtPair jwtPair = JwtUtils.issueRSATokenPair(
                JwkUtils.getPrivateRsaKey(),
                AuthConstants.ISSUER,
                principal.getUserId().toString(),
                claims,
                AuthConstants.ACCESS_TOKEN_EXPIRATION_OFFSET,
                AuthConstants.REFRESH_TOKEN_EXPIRATION_OFFSET
        );

        TokenResponseUtils.respondJwtPair(response, jwtPair);
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
