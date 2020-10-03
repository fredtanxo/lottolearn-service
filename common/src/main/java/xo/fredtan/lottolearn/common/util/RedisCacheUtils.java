package xo.fredtan.lottolearn.common.util;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@ConditionalOnClass(RedisOperations.class)
public class RedisCacheUtils {
    /**
     * 批量从Redis Zset中获取对象，没有命中则从数据库获取并放入缓存
     * @param list 批量获取的对象ID集合（有序）
     * @param keyPrefix 对象存入Redis的key前缀，最终的key值为：keyPrefix + id
     * @param expiration 对象存入Redis的过期时间
     * @param clazz 对象的Class
     * @param template key类型为String、value类型为byte[]的RedisTemplate
     * @param getter 从数据库获取单个未命中缓存的对象
     * @param <T> 对象的类型
     * @return 获取到的对象集合
     */
    public static <T> List<T> batchGet(List<String> list,
                                       String keyPrefix,
                                       Duration expiration,
                                       Class<T> clazz,
                                       RedisTemplate<String, byte[]> template,
                                       Function<Long, T> getter) {
        ValueOperations<String, byte[]> ops = template.opsForValue();

        List<byte[]> bytes = ops.multiGet(list.stream().map(id -> keyPrefix + id).collect(Collectors.toList()));
        if (Objects.isNull(bytes)) {
            return null;
        }

        int i = 0;
        for (byte[] b : bytes) {
            if (Objects.isNull(b) || b.length == 0) {
                String id = list.get(i);
                T result = getter.apply(Long.valueOf(id));
                if (Objects.nonNull(result)) {
                    byte[] serialize = ProtostuffSerializeUtils.serialize(result);
                    ops.set(keyPrefix + id, serialize, expiration);
                    bytes.set(i, serialize);
                }
            }
            i++;
        }

        return bytes
                .stream()
                .map(b -> ProtostuffSerializeUtils.deserialize(b, clazz))
                .collect(Collectors.toList());
    }

    /**
     * 批量将对象的ID存入Redis Zset，编号从0开始
     * @param key redis key
     * @param expiration key的过期时间
     * @param list 对象的ID集合
     * @param template key、value类型均为String的RedisTemplate
     */
    public static void batchSet(String key, Duration expiration, List<String> list, RedisTemplate<String, String> template) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return;
        }
        BoundZSetOperations<String, String> ops = template.boundZSetOps(key);
        int i = 0;
        Set<ZSetOperations.TypedTuple<String>> set = new HashSet<>();
        for (String id : list) {
            set.add(new DefaultTypedTuple<>(id, (double) i++));
        }
        ops.add(set);
        ops.expire(expiration);
    }

    /**
     * 根据pattern清除缓存
     * @param pattern key pattern
     * @param template RedisTemplate
     */
    public static void clearCache(String pattern, RedisTemplate<String, ?> template) {
        Set<String> keys = template.keys(pattern);
        if (Objects.nonNull(keys) && keys.size() > 0) {
            template.delete(keys);
        }
    }
}
