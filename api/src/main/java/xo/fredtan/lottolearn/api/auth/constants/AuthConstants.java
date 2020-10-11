package xo.fredtan.lottolearn.api.auth.constants;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AuthConstants {
    /* JWK */
    public static final String JWK_SET_LOCK_KEY = "lock:jwk";
    public static final Long JWK_SET_LOCK_KEY_EXPIRATION = 5L;
    public static final Integer JWK_SET_LOCK_KEY_MAX_RETRIES = 3;
    public static final String JWK_CACHE_KEY = "jwk";
    public static final String JWK_SET_URL = "/.well-known/jwks.json";

    /* JWT */
    public static final String ISSUER = "https://lottolearn.com";
    public static final Long ACCESS_TOKEN_EXPIRATION_OFFSET = TimeUnit.DAYS.toMillis(7L);
    public static final Long REFRESH_TOKEN_EXPIRATION_OFFSET = TimeUnit.DAYS.toMillis(30L);
    public static final String LOGIN_URL = "/auth/login";
    public static final String REFRESH_URL = "/auth/refresh";
    public static final String LOGOUT_URL = "/auth/logout";
    public static final String TOKEN_CLAIM_KEY = "scope";
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String ACCESS_TOKEN_EXPIRATION_KEY = "access_token_expiration";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_DOMAIN = "lottolearn.com";
    public static final String REFRESH_TOKEN_COOKIE_DOMAIN = "auth.lottolearn.com";
    public static final String TOKEN_COOKIE_PATH = "/";

    /* Auth */
    public static final String HOME_PAGE_ORIGIN = "https://lottolearn.com";
    public static final String SYSTEM_MANAGEMENT_ORIGIN = "https://system.lottolearn.com";
    public static final Set<String> ORIGIN_LIST = Set.of(HOME_PAGE_ORIGIN, SYSTEM_MANAGEMENT_ORIGIN);
}
