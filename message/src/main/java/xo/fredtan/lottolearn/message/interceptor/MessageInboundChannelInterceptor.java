package xo.fredtan.lottolearn.message.interceptor;

import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.course.constant.CourseConstants;
import xo.fredtan.lottolearn.api.message.constant.MessageConstants;
import xo.fredtan.lottolearn.api.message.service.MessageService;
import xo.fredtan.lottolearn.domain.course.UserCourse;

import java.security.Principal;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息传入拦截器
 */
public class MessageInboundChannelInterceptor implements ChannelInterceptor {
    private final JwtDecoder jwtDecoder;
    private final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter;
    private final MessageService messageService;
    private final RedisTemplate<String, String> redisTemplate;

    public MessageInboundChannelInterceptor(JwtDecoder jwtDecoder,
                                            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter,
                                            MessageService messageService,
                                            RedisTemplate<String, String> redisTemplate) {
        this.jwtDecoder = jwtDecoder;
        this.grantedAuthoritiesConverter = grantedAuthoritiesConverter;
        this.messageService = messageService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // 不能直接wrap出StompHeaderAccessor，否则后续权限校验永远失败
        // StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (Objects.isNull(headerAccessor)) {
            throw new MessagingException("没有提供Header");
        }
        StompCommand command = headerAccessor.getCommand();
        if (Objects.nonNull(command)) {
            switch (headerAccessor.getCommand()) {
                case CONNECT -> {
                    String jwt = headerAccessor.getFirstNativeHeader("authorization");
                    if (StringUtils.hasText(jwt)) {
                        Jwt decodedJwt = jwtDecoder.decode(jwt);
                        Collection<GrantedAuthority> authorities = grantedAuthoritiesConverter.convert(decodedJwt);
                        JwtAuthenticationToken authentication = new JwtAuthenticationToken(decodedJwt, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        headerAccessor.setUser(authentication);
                    }
                }
                case SUBSCRIBE -> {
                    String roomId = this.extractRoomId(headerAccessor);
                    if (StringUtils.isEmpty(roomId)) {
                        throw new MessagingException("必须提供房间号");
                    }

                    Principal user = headerAccessor.getUser();
                    if (Objects.isNull(user)) {
                        throw new MessagingException("未登录");
                    }
                    String userId = user.getName();
                    UserCourse userCourse = messageService.findUserCourseLive(userId, roomId);
                    if (Objects.isNull(userCourse)) {
                        throw new MessagingException("课程未开始或没有加入课程");
                    }
                    headerAccessor.setHeader(MessageConstants.COURSE_ID, userCourse.getCourseId());
                }
            }
        }

        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (Objects.isNull(headerAccessor)) {
            return;
        }
        StompCommand command = headerAccessor.getCommand();
        if (Objects.nonNull(command)) {
            switch (command) {
                case SUBSCRIBE -> {
                    this.userSubscribeEvent(headerAccessor, true);
                }
                case UNSUBSCRIBE, DISCONNECT -> {
                    this.userSubscribeEvent(headerAccessor, false);
                }
            }
        }
    }

    private void userSubscribeEvent(StompHeaderAccessor accessor, Boolean flag) {
        String roomId = this.extractRoomId(accessor);
        String courseId = (String) accessor.getHeader(MessageConstants.COURSE_ID);
        if (StringUtils.isEmpty(courseId) || StringUtils.isEmpty(roomId)) {
            return;
        }
        Principal principal = accessor.getUser();
        if (Objects.isNull(principal)) {
            return;
        }
        String userId = principal.getName();

        BoundSetOperations<String, String> ops =
                redisTemplate.boundSetOps(CourseConstants.LIVE_KEY_PREFIX + courseId + ":" + roomId);
        if (flag) {
            ops.add(userId);
        } else {
            ops.remove(userId);
        }
    }

    private String extractRoomId(StompHeaderAccessor accessor) {
        String destination = accessor.getFirstNativeHeader("destination");
        if (Objects.isNull(destination)) {
            return null;
        }
        Pattern pattern = Pattern.compile("/out/(\\w+.*|-.*)+"); // 匹配房间ID（UUID）
        Matcher matcher = pattern.matcher(destination);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
