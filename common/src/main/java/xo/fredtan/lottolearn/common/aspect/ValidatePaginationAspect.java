package xo.fredtan.lottolearn.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 验证分页参数，如验证失败，设置{@code page}为0、{@code size}为20
 */
@Aspect
@Component
public class ValidatePaginationAspect {
    @Pointcut("@annotation(xo.fredtan.lottolearn.common.annotation.ValidatePagination)")
    public void validatePagination() {
    }

    @Around("validatePagination()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] joinPointArgs = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            if ("page".equals(parameterNames[i])) {
                if (Objects.isNull(joinPointArgs[i]) || (Integer) joinPointArgs[i] < 0) {
                    joinPointArgs[i] = 0;
                }
            } else if ("size".equals(parameterNames[i])) {
                if (Objects.isNull(joinPointArgs[i]) || (Integer) joinPointArgs[i] < 0) {
                    joinPointArgs[i] = 20;
                }
            }
        }

        return joinPoint.proceed(joinPointArgs);
    }
}
