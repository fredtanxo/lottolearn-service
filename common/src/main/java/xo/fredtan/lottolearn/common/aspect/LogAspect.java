package xo.fredtan.lottolearn.common.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xo.fredtan.lottolearn.common.async.AsyncLogger;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
@Component
@ConditionalOnClass({ RabbitTemplate.class, DefaultAuthenticationEventPublisher.class })
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LogAspect {
    private final AsyncLogger asyncLogger;

    @Pointcut("@annotation(xo.fredtan.lottolearn.common.annotation.Log)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object doAfter(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(requestAttributes)).getRequest();
        asyncLogger.saveLog(joinPoint, authentication, request);
        return result;
    }
}
