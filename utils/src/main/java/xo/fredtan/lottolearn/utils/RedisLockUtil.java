package xo.fredtan.lottolearn.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
public class RedisLockUtil {
    private static final String UNLOCK_LUA;

    static {
        UNLOCK_LUA = "if redis.call('get',KEYS[1]) == ARGV[1] " +
                    "then " +
                    "    return redis.call('del',KEYS[1]) " +
                    "else " +
                    "    return 0 " +
                    "end ";
    }

    /**
     * 获取Redis锁并执行操作，执行完毕后自动释放锁
     * @param template RedisTemplate
     * @param lockKey 锁Key
     * @param lockIdentifier 锁Value
     * @param duration 锁过期时间
     * @param retries 重试次数（总获取次数 = 重试次数 + 1）
     * @param retryGap 重试间隔（毫秒）
     * @param action 要执行的操作
     * @param arg 执行操作所需的参数
     * @param <T> 执行操作所需的参数的类型
     * @return 执行是否成功
     */
    public static <T> boolean tryLockAndStart(RedisTemplate<String, String> template,
                                                String lockKey,
                                                String lockIdentifier,
                                                Duration duration,
                                                int retries,
                                                long retryGap,
                                                Consumer<T> action,
                                                T arg) {
        boolean success = false;
        int doRetries = -1;
        while (doRetries++ < retries) {
            success = Objects.requireNonNullElse(
                    template.opsForValue().setIfAbsent(lockKey, lockIdentifier, duration),
                    false);
            if (success) {
                try {
                    action.accept(arg);
                } finally {
                    RedisLockUtil.unlock(template, lockKey, lockIdentifier);
                }
                break;
            }
            try {
                Thread.sleep(retryGap);
            } catch (InterruptedException e) {
                log.warn("线程[{}]在sleep过程中被打断", Thread.currentThread().getName());
            }
            log.error("获取Redis锁[{}]失败 ({}/{})", lockKey, doRetries, retries);
        }

        return success;
    }


    /**
     * 释放Redis锁
     * @param template RedisTemplate
     * @param lockKey 锁Key
     * @param lockIdentifier 锁Value
     */
    public static void unlock(RedisTemplate<String, String> template, String lockKey, String lockIdentifier) {
        RedisCallback<Long> callback = connection -> connection.eval(UNLOCK_LUA.getBytes(),
                                                                        ReturnType.INTEGER,
                                                                        1,
                                                                        lockKey.getBytes(),
                                                                        lockIdentifier.getBytes());

        template.execute(callback);
    }
}
