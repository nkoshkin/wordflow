package io.ylab.wordflow.audit;

import io.ylab.wordflow.service.impl.IAuditService;
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

    private final IAuditService auditService;

    @Pointcut("@annotation(io.ylab.wordflow.audit.Auditable)")
    public void auditableMethods() {}

    @AfterReturning(pointcut = "auditableMethods()", returning = "result")
    public void audit(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Auditable auditable = signature.getMethod().getAnnotation(Auditable.class);
        String action = auditable.action();

        Object[] args = joinPoint.getArgs();
        String parameters = extractParameters(args);

        UUID analysisId = extractAnalysisId(result, args);

        auditService.logAnalysis(action, parameters, analysisId);
    }

    private String extractParameters(Object[] args) {
        if (args == null || args.length == 0) return "";
        return args[0] != null ? args[0].toString() : "";
    }

    private UUID extractAnalysisId(Object result, Object[] args) {
        if (result != null && result instanceof UUID) {
            return (UUID) result;
        }
        for (Object arg : args) {
            if (arg instanceof UUID) {
                return (UUID) arg;
            }
        }
        return null;
    }
}
