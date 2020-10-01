package xo.fredtan.lottolearn.message.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import xo.fredtan.lottolearn.api.message.constants.MessageConstants;
import xo.fredtan.lottolearn.message.listener.RedisMessageListener;

import java.util.Collections;
import java.util.Map;

@Configuration
public class RedisReceiverConfig {
    private final RedisMessageListener messageListener;

    @Autowired
    public RedisReceiverConfig(RedisMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessageListeners(
                Map.of(messageListener, Collections.singletonList(new PatternTopic(MessageConstants.LIVE_DISTRIBUTION_CHANNEL)))
        );
        return container;
    }
}
