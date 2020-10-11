package xo.fredtan.lottolearn.auth.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import xo.fredtan.lottolearn.domain.auth.JwtPair;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class JwtUtils {
    /**
     * 签发RSA签名的JWT对
     * @param rsaKey 私钥
     * @param issuer 签发者
     * @param subject 签发对象
     * @param claims Claims
     * @param accessTokenExpirationOffset access_token过期时间
     * @param refreshTokenExpirationOffset refresh_token过期时间
     * @return the {@link JwtPair}
     */
    public static JwtPair issueRSATokenPair(RSAKey rsaKey,
                                            String issuer,
                                            String subject,
                                            Map<String, ?> claims,
                                            Long accessTokenExpirationOffset,
                                            Long refreshTokenExpirationOffset) {
        Date now = new Date();
        JwtPair.AccessToken accessToken = new JwtPair.AccessToken();
        accessToken.setToken(issueRSAToken(rsaKey, issuer, subject, claims, now, accessTokenExpirationOffset));
        accessToken.setExpiration(now.getTime() + accessTokenExpirationOffset);

        JwtPair.RefreshToken refreshToken = new JwtPair.RefreshToken();
        refreshToken.setToken(issueRSAToken(rsaKey, issuer, subject, claims, now, refreshTokenExpirationOffset));
        refreshToken.setExpiration(now.getTime() + refreshTokenExpirationOffset);

        JwtPair jwtPair = new JwtPair();
        jwtPair.setAccessToken(accessToken);
        jwtPair.setRefreshToken(refreshToken);

        return jwtPair;
    }
    /**
     * 签发RSA签名的JWT
     * @param rsaKey 私钥
     * @param issuer 签发者
     * @param subject 签发对象
     * @param claims Claims
     * @param expirationOffset 过期偏移时间
     * @return JWT
     */
    public static String issueRSAToken(RSAKey rsaKey,
                                       String issuer,
                                       String subject,
                                       Map<String, ?> claims,
                                       Date issueTime,
                                       Long expirationOffset) {
        try {
            JWSSigner signer = new RSASSASigner(rsaKey);
            JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder()
                    .subject(String.valueOf(subject))
                    .issuer(issuer)
                    .issueTime(issueTime)
                    .jwtID(UUID.randomUUID().toString())
                    .expirationTime(new Date(issueTime.getTime() + expirationOffset));

            for (Map.Entry<String, ?> entry : claims.entrySet()) {
                claimsSetBuilder.claim(entry.getKey(), entry.getValue());
            }

            JWTClaimsSet claimsSet = claimsSetBuilder.build();

            String kid = rsaKey.getKeyID();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(kid).build(), claimsSet);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException ignored) {
        }
        return null;
    }

    /**
     * 验证JWT
     * @param jwkSet JWKSet
     * @param token 要验证的JWT
     * @return the {@link JWTClaimsSet}
     */
    public static JWTClaimsSet verifyRSAToken(JWKSet jwkSet, String token) {
        for (JWK key : jwkSet.getKeys()) {
            if (!KeyType.RSA.equals(key.getKeyType())) {
                continue;
            }
            try {
                RSAKey rsaKey = (RSAKey) key;
                RSASSAVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());
                SignedJWT signedJWT = SignedJWT.parse(token);
                if (signedJWT.verify(verifier)) {
                    return signedJWT.getJWTClaimsSet();
                }
            } catch (JOSEException | ParseException ignored) {
            }
        }
        return null;
    }
}
