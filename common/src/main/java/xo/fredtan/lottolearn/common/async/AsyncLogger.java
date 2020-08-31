package xo.fredtan.lottolearn.common.async;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.common.annotation.Log;
import xo.fredtan.lottolearn.common.config.LogMqConfig;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@EnableAsync
@Component
@ConditionalOnClass({RabbitTemplate.class, DefaultAuthenticationEventPublisher.class})
public class AsyncLogger {
    private static final DateFormat dateFormat;

    private final RabbitTemplate rabbitTemplate;

    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public AsyncLogger(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Async
    public void saveLog(ProceedingJoinPoint joinPoint, Authentication authentication, HttpServletRequest request) {
        Date now = new Date();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log annotation = method.getAnnotation(Log.class);

        Object principal = authentication.getPrincipal();
        String operatorName = "null";
        if (principal instanceof Jwt) {
            operatorName = ((Jwt) principal).getClaim("nickname");
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(ip)) {
            ip = request.getRemoteAddr();
        }

        String time = dateFormat.format(now);

        // 没必要添加model的依赖，折中一下直接做成Map就好
        Map<String, String> log = Map.of(
                "operation", annotation.value(),
                "method", method.getName(),
                "args", JSON.toJSONString(joinPoint.getArgs()),
                "operatorId", authentication.getName(),
                "operatorName", operatorName,
                "ip", ip,
                "time", time
        );

        rabbitTemplate.convertAndSend(LogMqConfig.EXCHANGE_SYSTEM_LOG, LogMqConfig.ROUTING_KEY_SYSTEM_LOG, log);
    }
}
