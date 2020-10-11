package xo.fredtan.lottolearn.auth.filter;

import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.auth.constants.AuthConstants;
import xo.fredtan.lottolearn.auth.domain.JwtRefreshAuthenticationToken;
import xo.fredtan.lottolearn.auth.util.JwkUtils;
import xo.fredtan.lottolearn.auth.util.JwtUtils;
import xo.fredtan.lottolearn.auth.util.TokenResponseUtils;
import xo.fredtan.lottolearn.domain.auth.JwtPair;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 刷新JWT的Filter
 */
public class JwtRefreshFilter extends AbstractAuthenticationProcessingFilter {
    public JwtRefreshFilter(AuthenticationManager authenticationManager) {
        super(AuthConstants.REFRESH_URL);
        super.setAuthenticationManager(authenticationManager);
    }

    /**
     * 刷新JWT，检验JWT有效性
     *
     * @param request  the {@link HttpServletRequest}
     * @param response the {@link HttpServletResponse}
     * @return the {@link JwtRefreshAuthenticationToken}
     * @throws AuthenticationException the {@link AuthenticationException}
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        String origin = request.getHeader("origin");
        if (StringUtils.hasText(origin) && !AuthConstants.ORIGIN_LIST.contains(origin)) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (Objects.nonNull(cookies)) {
            for (Cookie cookie : cookies) {
                if (AuthConstants.REFRESH_TOKEN_KEY.equals(cookie.getName())) {
                    JWTClaimsSet jwtClaimsSet = JwtUtils.verifyRSAToken(JwkUtils.getCachedJwk(), cookie.getValue());
                    return new JwtRefreshAuthenticationToken(jwtClaimsSet);
                }
            }
        }
        return null;
    }

    /**
     * JWT合法
     * 发放新的{@code access_token}和{@code refresh_token}
     *
     * @param request    the {@link HttpServletRequest}
     * @param response   the {@link HttpServletResponse}
     * @param chain      the {@link FilterChain}
     * @param authResult the {@link JwtRefreshAuthenticationToken}
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        JWTClaimsSet principal = (JWTClaimsSet) authResult.getPrincipal();
        String subject = principal.getSubject();
        JwtPair jwtPair = JwtUtils.issueRSATokenPair(
                JwkUtils.getPrivateRsaKey(),
                AuthConstants.ISSUER,
                subject,
                principal.getClaims(),
                AuthConstants.ACCESS_TOKEN_EXPIRATION_OFFSET,
                AuthConstants.REFRESH_TOKEN_EXPIRATION_OFFSET
        );

        TokenResponseUtils.respondJwtPair(response, jwtPair);
    }

    /**
     * JWT校验失败
     * 清除所有cookie并返回{@code 401}状态码
     *
     * @param request  the {@link HttpServletRequest}
     * @param response the {@link HttpServletResponse}
     * @param failed   the {@link AuthenticationException}
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) {
        TokenResponseUtils.clearCookies(request, response);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
