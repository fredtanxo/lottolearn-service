package xo.fredtan.lottolearn.message.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import xo.fredtan.lottolearn.api.message.constants.MessageConstants;
import xo.fredtan.lottolearn.api.message.service.MessageService;
import xo.fredtan.lottolearn.message.interceptor.MessageInboundChannelInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    private final MessageService messageService;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    public WebSocketSecurityConfig(MessageService messageService, RedisTemplate<String, String> redisTemplate) {
        this.messageService = messageService;
        this.redisTemplate = redisTemplate;
    }

    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    public JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter() {
        return new JwtGrantedAuthoritiesConverter();
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/classroom")
                .setAllowedOrigins("https://lottolearn.com");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 10秒发送心跳（Stompjs默认设置）
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("wss-heartbeat-");
        scheduler.initialize();

        registry.enableSimpleBroker("/in", "/out")
                .setHeartbeatValue(new long[] {MessageConstants.HEARTBEAT_INCOMING, MessageConstants.HEARTBEAT_OUTGOING})
                .setTaskScheduler(scheduler);
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .nullDestMatcher().permitAll()
                .anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    protected void customizeClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                new MessageInboundChannelInterceptor(
                        jwtDecoder(), grantedAuthoritiesConverter(), messageService, redisTemplate)
        );
    }
}
