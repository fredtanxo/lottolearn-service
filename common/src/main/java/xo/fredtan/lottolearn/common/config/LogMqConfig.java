package xo.fredtan.lottolearn.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RabbitTemplate.class)
public class LogMqConfig {
    public static final String EXCHANGE_SYSTEM_LOG = "exchange_system_log";
    public static final String QUEUE_SYSTEM_LOG = "queue_system_log";
    public static final String ROUTING_KEY_SYSTEM_LOG = "routing_key_system_log";

    @Bean(EXCHANGE_SYSTEM_LOG)
    public Exchange systemLogExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_SYSTEM_LOG).durable(true).build();
    }

    @Bean(QUEUE_SYSTEM_LOG)
    public Queue systemLogQueue() {
        return QueueBuilder.durable(QUEUE_SYSTEM_LOG).build();
    }

    @Bean
    public Binding systemLogBinding(@Qualifier(EXCHANGE_SYSTEM_LOG) Exchange exchange,
                                    @Qualifier(QUEUE_SYSTEM_LOG) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_SYSTEM_LOG).noargs();
    }
}
