package xo.fredtan.lottolearn.processor.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

public class ProcessorMqConfig {
    public static final String EXCHANGE_MEDIA_PROCESS = "ex_media_process";
    public static final String QUEUE_MEDIA_PROCESS = "queue_media_process";
    public static final String ROUTING_KEY_MEDIA_PROCESS = "routing_key_media_process";

    @Bean(EXCHANGE_MEDIA_PROCESS)
    public Exchange mediaProcessExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_MEDIA_PROCESS).durable(true).build();
    }

    @Bean(QUEUE_MEDIA_PROCESS)
    public Queue mediaProcessQueue() {
        return QueueBuilder.durable(QUEUE_MEDIA_PROCESS).build();
    }

    @Bean
    public Binding mediaProcessBinding(@Qualifier(EXCHANGE_MEDIA_PROCESS) Exchange exchange,
                                       @Qualifier(QUEUE_MEDIA_PROCESS) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_MEDIA_PROCESS).noargs();
    }
}
