package xo.fredtan.lottolearn.course.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    /* 课程签到 */
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


    /* 创建课程 */
    public static final String EXCHANGE_ADD_COURSE = "ex_add_course";
    public static final String QUEUE_ADD_COURSE = "queue_add_course";
    public static final String ROUTING_KEY_ADD_COURSE = "routing_key_add_course";

    @Bean(EXCHANGE_ADD_COURSE)
    public Exchange addCourseExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_ADD_COURSE).durable(true).build();
    }

    @Bean(QUEUE_ADD_COURSE)
    public Queue addCourseQueue() {
        return QueueBuilder.durable(QUEUE_ADD_COURSE).build();
    }

    @Bean
    public Binding addCourseBinding(@Qualifier(EXCHANGE_ADD_COURSE) Exchange exchange,
                                    @Qualifier(QUEUE_ADD_COURSE) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_ADD_COURSE).noargs();
    }


    /* 加入课程 */
    public static final String EXCHANGE_JOIN_COURSE = "ex_join_course";
    public static final String QUEUE_JOIN_COURSE = "queue_join_course";
    public static final String ROUTING_KEY_JOIN_COURSE = "routing_key_join_course";

    @Bean(EXCHANGE_JOIN_COURSE)
    public Exchange joinCourseExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_JOIN_COURSE).durable(true).build();
    }

    @Bean(QUEUE_JOIN_COURSE)
    public Queue joinCourseQueue() {
        return QueueBuilder.durable(QUEUE_JOIN_COURSE).build();
    }

    @Bean
    public Binding joinCourseBinding(@Qualifier(EXCHANGE_JOIN_COURSE) Exchange exchange,
                                     @Qualifier(QUEUE_JOIN_COURSE) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_JOIN_COURSE).noargs();
    }
}
