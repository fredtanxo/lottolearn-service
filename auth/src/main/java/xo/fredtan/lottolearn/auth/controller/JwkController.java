package xo.fredtan.lottolearn.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xo.fredtan.lottolearn.api.auth.controller.JwkControllerApi;
import xo.fredtan.lottolearn.auth.util.JwkUtils;

import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class JwkController implements JwkControllerApi {
    private static final AtomicBoolean checked = new AtomicBoolean(false);

    @GetMapping("/.well-known/jwks.json")
    public String fetchJWKSet() {
        // 确保当前Application使用的公钥已经存储到JWKSet中
        if (!checked.get()) {
            synchronized (JwkController.class) {
                if (!checked.get()) {
                    if (JwkUtils.cacheJwkSet()) {
                        checked.set(true);
                    }
                }
            }
        }
        return JwkUtils.getCachedJwkString();
    }
}
