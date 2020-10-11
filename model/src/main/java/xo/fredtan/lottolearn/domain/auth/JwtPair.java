package xo.fredtan.lottolearn.domain.auth;

import lombok.Data;

@Data
public class JwtPair {
    @Data
    public static final class AccessToken {
        String token;
        Long expiration;
    }

    @Data
    public static final class RefreshToken {
        String token;
        Long expiration;
    }

    private AccessToken accessToken;
    private RefreshToken refreshToken;
}
