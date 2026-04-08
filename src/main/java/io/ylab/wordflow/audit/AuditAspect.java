package io.ylab.wordflow.audit;

import io.ylab.wordflow.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @Pointcut("@annotation(io.ylab.wordflow.audit.Auditable)")
    public void auditableMethods() {}

    @AfterReturning(pointcut = "auditableMethods()", returning = "result")
    public void audit(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Auditable auditable = signature.getMethod().getAnnotation(Auditable.class);
        String action = auditable.action();

        // Извлекаем параметры метода (например, RequestDto или id)
        Object[] args = joinPoint.getArgs();
        String parameters = extractParameters(args);

        // Если метод возвращает id (например, анализ запущен), можно сохранить analysisId
        String analysisId = extractAnalysisId(result, args);

        auditService.log(action, parameters, analysisId);
    }

    private String extractParameters(Object[] args) {
        if (args == null || args.length == 0) return "";
        // Можно преобразовать в JSON, но для простоты – toString()
        return args[0] != null ? args[0].toString() : "";
    }

    private String extractAnalysisId(Object result, Object[] args) {
        if (result != null && result instanceof UUID) {
            return ((UUID) result).toString();
        }
        // Если результат не UUID, возможно id лежит в параметрах (например, GET /results/{id})
        for (Object arg : args) {
            if (arg instanceof UUID) {
                return arg.toString();
            }
        }
        return null;
    }
}
