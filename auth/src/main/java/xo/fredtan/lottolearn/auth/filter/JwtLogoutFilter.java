package xo.fredtan.lottolearn.auth.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import xo.fredtan.lottolearn.api.auth.constants.AuthConstants;
import xo.fredtan.lottolearn.auth.util.TokenResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtLogoutFilter extends AbstractAuthenticationProcessingFilter {
    public JwtLogoutFilter(AuthenticationManager authenticationManager) {
        super(AuthConstants.LOGOUT_URL);
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        TokenResponseUtils.clearCookies(request, response);
        response.setStatus(HttpServletResponse.SC_OK);
        return null;
    }
}
