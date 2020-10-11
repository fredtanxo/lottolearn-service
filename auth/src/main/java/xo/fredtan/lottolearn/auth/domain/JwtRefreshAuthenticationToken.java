package xo.fredtan.lottolearn.auth.domain;

import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

public class JwtRefreshAuthenticationToken extends AbstractAuthenticationToken {
    private final JWTClaimsSet principal;
    public JwtRefreshAuthenticationToken(JWTClaimsSet claimsSet) {
        this(null, claimsSet);
    }

    public JwtRefreshAuthenticationToken(Collection<? extends GrantedAuthority> authorities, JWTClaimsSet claimsSet) {
        super(authorities);
        this.principal = claimsSet;
        setAuthenticated(Objects.nonNull(claimsSet));
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public JWTClaimsSet getPrincipal() {
        return this.principal;
    }
}
