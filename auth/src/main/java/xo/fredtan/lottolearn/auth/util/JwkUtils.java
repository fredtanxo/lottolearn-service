package xo.fredtan.lottolearn.auth.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.auth.constants.AuthConstants;
import xo.fredtan.lottolearn.common.util.RedisLockUtils;

import java.io.*;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class JwkUtils {
    private static RedisTemplate<String, String> redisTemplate;

    private static String appName;
    private static String keyStoreDir;

    private static volatile RSAKey privateKey = null;

    @Value("${spring.application.name}")
    public void setAppName(String name) {
        appName = name;
    }

    @Value("${lottolearn.auth.key-store-dir}")
    public void setKeyStoreDir(String dir) {
        keyStoreDir = dir;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> template) {
        redisTemplate = template;
    }

    /**
     * 获取RSA私钥，优先从文件获取，获取不到则直接生成
     * @return RSA私钥
     */
    public static RSAKey getPrivateRsaKey() {
        if (Objects.isNull(privateKey)) {
            synchronized (JwkUtils.class) {
                if (Objects.isNull(privateKey)) {
                    privateKey = getPrivateRsaKeyFromFile();
                    if (Objects.isNull(privateKey)) {
                        privateKey = generatePrivateRsaKey();
                    }
                }
            }
        }
        return privateKey;
    }

    /**
     * 从文件加载RSA私钥
     * @return RSA私钥
     */
    private static RSAKey getPrivateRsaKeyFromFile() {
        if (!StringUtils.hasText(keyStoreDir)) {
            return null;
        }
        File privateKeyFile = Paths.get(keyStoreDir).resolve("private.key").toFile();
        if (!privateKeyFile.exists() || !privateKeyFile.isFile()) {
            return null;
        }
        RSAKey key = null;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(privateKeyFile))) {
            String k = bufferedReader.lines().collect(Collectors.joining());
            key = RSAKey.parse(k);
        } catch (IOException | ParseException ignored) {
        }
        return key;
    }

    /**
     * 生成RSA私钥，并尝试保存到文件中
     * @return RSA私钥
     */
    private static RSAKey generatePrivateRsaKey() {
        RSAKey rsaKey = null;
        try {
            rsaKey = new RSAKeyGenerator(RSAKeyGenerator.MIN_KEY_SIZE_BITS)
                    .keyID(appName + "-" + new Date().getTime())
                    .generate();
            storePrivateRsaKey(rsaKey);
        } catch (JOSEException ignored) {
        }
        return rsaKey;
    }

    /**
     * 保存RSA私钥到文件，如文件已存在则覆盖
     * @param rsaKey RSA私钥
     */
    private static void storePrivateRsaKey(RSAKey rsaKey) {
        if (!StringUtils.hasText(keyStoreDir)) {
            return;
        }
        File file = Paths.get(keyStoreDir).resolve("private.key").toFile();
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                return;
            }
        }
        String key = rsaKey.toString();
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            outputStream.write(key.getBytes());
        } catch (IOException ignored) {
        }
    }

    /**
     * 获取RSA公钥
     * @return RSA公钥
     */
    public static RSAKey getPublicRsaKey() {
        if (Objects.isNull(privateKey)) {
            getPrivateRsaKey();
        }
        return privateKey.toPublicJWK();
    }

    /**
     * 将RSA公钥保存到JWKSet并缓存
     * @return 操作是否成功
     */
    public static boolean cacheJwkSet() {
        RSAKey publicJWK = getPublicRsaKey();
        // 将PublicKey存入JWKSet
        String identifier = UUID.randomUUID().toString();
        Consumer<RSAKey> action = key -> {
            BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(AuthConstants.JWK_CACHE_KEY);
            String jwkSetSerialized = ops.get();

            JWKSet jwkSet = new JWKSet(key);
            if (StringUtils.hasText(jwkSetSerialized)) {
                try {
                    jwkSet = JWKSet.parse(jwkSetSerialized);
                } catch (ParseException ignored) {
                }
            }

            try {
                if (!jwkSet.containsJWK(key)) {
                    List<JWK> jwkList = new ArrayList<>(jwkSet.getKeys());
                    jwkList.add(key);
                    jwkSet = new JWKSet(jwkList);
                }
            } catch (JOSEException ignored) {
            }

            ops.set(jwkSet.toString());
        };

        return RedisLockUtils.tryLockAndStart(
                redisTemplate,
                AuthConstants.JWK_SET_LOCK_KEY,
                identifier,
                Duration.ofSeconds(AuthConstants.JWK_SET_LOCK_KEY_EXPIRATION),
                AuthConstants.JWK_SET_LOCK_KEY_MAX_RETRIES,
                TimeUnit.SECONDS.toMillis(3),
                action,
                publicJWK
        );
    }

    /**
     * 从缓存获取JWKSet
     * @return the {@link JWKSet}
     */
    public static JWKSet getCachedJwk() {
        String jwk = getCachedJwkString();
        JWKSet jwkSet = null;
        try {
            jwkSet = JWKSet.parse(jwk);
        } catch (ParseException ignored) {
        }
        return jwkSet;
    }

    /**
     * 从缓存获取已经序列化的JWKSet
     * @return the {@link JWKSet}
     */
    public static String getCachedJwkString() {
        BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(AuthConstants.JWK_CACHE_KEY);
        String jwkSet = ops.get();
        if (!StringUtils.hasText(jwkSet)) {
            synchronized (JwkUtils.class) {
                if (!StringUtils.hasText(jwkSet = ops.get())) {
                    cacheJwkSet();
                    jwkSet = ops.get();
                }
            }
        }
        return jwkSet;
    }
}
