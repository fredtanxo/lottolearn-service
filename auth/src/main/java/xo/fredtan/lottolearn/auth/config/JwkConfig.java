package xo.fredtan.lottolearn.auth.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.auth.constant.AuthConstants;
import xo.fredtan.lottolearn.utils.RedisLockUtil;

import java.text.ParseException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Configuration
public class JwkConfig {
    private final RedisTemplate<String, String> redisTemplate;
    private String kid;

    @Value("${spring.application.name}")
    public void setKid(String appName) {
        this.kid = appName.concat(String.valueOf(new Date().getTime()));
    }

    @Autowired
    public JwkConfig(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public RSAKey privateJWK() throws JOSEException {
        RSAKey rsaKey = new RSAKeyGenerator(2048).keyID(kid).generate();
        RSAKey publicJWK = rsaKey.toPublicJWK();

        // 将PublicKey存入JWKSet
        String identifier = UUID.randomUUID().toString();
        Consumer<RSAKey> action = key -> {
            BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(AuthConstants.JWK_STORE_KEY);
            String jwkSetSerialized = ops.get();

            JWKSet jwkSet = null;
            if (StringUtils.hasText(jwkSetSerialized)) {
                try {
                    jwkSet = JWKSet.parse(jwkSetSerialized);
                } catch (ParseException e) {
                    log.error("转换JWKSet出错");
                }
            }

            if (Objects.isNull(jwkSet)) {
                jwkSet = new JWKSet(key);
            } else {
                List<JWK> jwkList = new ArrayList<>(jwkSet.getKeys());
                jwkList.add(key);
                jwkSet = new JWKSet(jwkList);
            }

            ops.set(jwkSet.toString());
        };

        boolean result = RedisLockUtil.tryLockAndStart(redisTemplate,
                AuthConstants.JWK_SET_LOCK_KEY,
                identifier,
                Duration.ofSeconds(AuthConstants.JWK_SET_LOCK_KEY_EXPIRATION),
                AuthConstants.JWK_SET_LOCK_KEY_MAX_RETRIES,
                TimeUnit.SECONDS.toMillis(3),
                action,
                publicJWK);

        if (!result) {
            throw new RuntimeException("无法写入JWKSet");
        }

        return rsaKey;
    }
}
