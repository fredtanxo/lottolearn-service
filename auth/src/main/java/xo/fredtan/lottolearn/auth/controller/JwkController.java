package xo.fredtan.lottolearn.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xo.fredtan.lottolearn.api.auth.constant.AuthConstants;

@RestController
public class JwkController {
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public JwkController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/.well-known/jwks.json")
    public String fetchJWKS() {
        return redisTemplate.opsForValue().get(AuthConstants.JWK_STORE_KEY);
    }
}
