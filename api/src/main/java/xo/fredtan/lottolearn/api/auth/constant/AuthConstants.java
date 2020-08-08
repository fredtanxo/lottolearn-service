package xo.fredtan.lottolearn.api.auth.constant;

import java.util.concurrent.TimeUnit;

public class AuthConstants {
    public static final String JWK_SET_LOCK_KEY = "LOCK:JWK";
    public static final Long JWK_SET_LOCK_KEY_EXPIRATION = 5L;
    public static final Integer JWK_SET_LOCK_KEY_MAX_RETRIES = 3;
    public static final String JWK_STORE_KEY = "JWK";
    public static final String ISSUER = "https://lottolearn.com";
    public static final Long EXPIRATION_OFFSET = TimeUnit.DAYS.toMillis(7L);
    public static final String LOGIN_URL = "/auth/login";
    public static final String TOKEN_CLAIM_KEY = "scope";
    public static final String TOKEN_RESPONSE_HEADER = "Authorization";
    public static final String TOKEN_RESPONSE_PREFIX = "Bearer ";
}
