package xo.fredtan.lottolearn.course.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String EXCHANGE_COURSE_SIGN = "ex_course_sign";
    public static final String QUEUE_COURSE_SIGN = "queue_course_sign";
    public static final String ROUTING_KEY_COURSE_SIGN = "routing_key_course_sign";

    @Bean(EXCHANGE_COURSE_SIGN)
    public Exchange courseSignExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_COURSE_SIGN).durable(true).build();
    }

    @Bean(QUEUE_COURSE_SIGN)
    public Queue courseSignQueue() {
        return QueueBuilder.durable(QUEUE_COURSE_SIGN).build();
    }

    @Bean
    public Binding courseSignBinding(@Qualifier(EXCHANGE_COURSE_SIGN) Exchange exchange,
                                     @Qualifier(QUEUE_COURSE_SIGN) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_COURSE_SIGN).noargs();
    }
}
