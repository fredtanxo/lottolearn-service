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

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class JwtUtil {
    /**
     * 签发RSA签名的JWT
     * @param rsaKey 私钥
     * @param issuer 签发者
     * @param subject 签发对象
     * @param claims 其他信息
     * @param expirationOffset 过期时间
     * @return JWT
     */
    public static String issueRSAToken(RSAKey rsaKey,
                                       String issuer,
                                       String subject,
                                       Map.Entry<String, ?> claims,
                                       Long expirationOffset) {
        Date now = new Date();
        try {
            JWSSigner signer = new RSASSASigner(rsaKey);
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .claim(claims.getKey(), claims.getValue())
                    .issuer(issuer)
                    .issueTime(now)
                    .expirationTime(new Date(now.getTime() + expirationOffset))
                    .build();
            String kid = rsaKey.getKeyID();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(kid).build(), claimsSet);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 使用RSAKey验证JWT
     * @param jwkSet JWKSet
     * @param kid RSAKey在JWKSet中的ID
     * @param token 要验证的JWT
     * @return 验证是否通过
     */
    public static boolean verifyRSAToken(JWKSet jwkSet, String kid, String token) {
        RSAKey rsaKey = JwtUtil.jwkSetToRSA(jwkSet, kid);
        try {
            RSASSAVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.verify(verifier);
        } catch (JOSEException | ParseException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static RSAKey jwkSetToRSA(JWKSet jwkSet, String kid) {
        JWK jwk = jwkSet.getKeyByKeyId(kid);
        if (Objects.isNull(jwk)) {
            log.error("JWT验证错误：在JWKSet中找不到对应ID的Key");
            throw new IllegalArgumentException("在JWKSet中找不到对应ID的Key");
        }
        if (!KeyType.RSA.equals(jwk.getKeyType())) {
            log.error("JWT验证错误：给定的Key非RSA类型，但请求使用RSA验证");
            throw new IllegalArgumentException("给定的Key非RSA类型");
        }
        return (RSAKey) jwk;
    }
}
