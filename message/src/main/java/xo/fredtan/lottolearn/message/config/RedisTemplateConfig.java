package xo.fredtan.lottolearn.message.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import xo.fredtan.lottolearn.domain.message.ChatMessage;

@Configuration
public class RedisTemplateConfig {
    @Bean
    public RedisTemplate<String, ChatMessage> redisMessageTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ChatMessage> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new FastJsonRedisSerializer<>(ChatMessage.class));
        return template;
    }
}
