package io.ylab.wordflow.audit;

import io.ylab.wordflow.service.IAuditService;
import io.ylab.wordflow.service.impl.AuditServiceImpl;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Аспект для автоматического аудита методов, помеченных аннотацией {@link Auditable}.
 * Перехватывает выполнение таких методов, извлекает параметры и результат,
 * и сохраняет запись в базу данных через {@link IAuditService}.
 *
 * <p>Логирует следующие данные:
 * <ul>
 *   <li>имя пользователя (из {@link SecurityContextHolder})</li>
 *   <li>время выполнения</li>
 *   <li>тип действия (из аннотации)</li>
 *   <li>параметры запроса (строковое представление аргументов)</li>
 *   <li>идентификатор анализа (извлекается из возвращаемого значения или параметров)</li>
 * </ul>
 * </p>
 *
 * <p>Аспект срабатывает после успешного возврата метода
 * ({@link org.aspectj.lang.annotation.AfterReturning}).</p>
 *
 * @see Auditable
 * @see IAuditService
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditServiceImpl auditService;

    /**
     * Точка pointcut для всех методов, аннотированных {@code @Auditable}.
     */
    @Pointcut("@annotation(io.ylab.wordflow.audit.Auditable)")
    public void auditableMethods() {}

    /**
     * Advice, выполняемый после успешного возврата аннотированного метода.
     *
     * <p>Извлекает из метода:
     * <ul>
     *   <li>значение {@code action} из аннотации</li>
     *   <li>параметры метода (преобразует в строку)</li>
     *   <li>идентификатор анализа (если метод возвращает UUID или принимает его как параметр)</li>
     * </ul>
     * </p>
     *
     * @param joinPoint точка соединения, содержащая информацию о вызванном методе
     * @param result результат, возвращённый методом (может быть {@code null})
     */
    @AfterReturning(pointcut = "auditableMethods()", returning = "result")
    public void audit(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Auditable auditable = signature.getMethod().getAnnotation(Auditable.class);
        String action = auditable.action();

        Object[] args = joinPoint.getArgs();
        String parameters = extractParameters(args);

        UUID analysisId = extractAnalysisId(result, args);

        auditService.log(action, parameters, analysisId);
    }

    /**
     * Извлекает строковое представление параметров метода.
     *
     * @param args массив аргументов метода
     * @return строковое представление
     */
    private String extractParameters(Object[] args) {
        if (args == null || args.length == 0) return "";
        return args[0] != null ? args[0].toString() : "";
    }

    /**
     * Извлекает идентификатор анализа из результата или параметров.
     *
     * @param result результат выполнения метода
     * @param args   аргументы метода
     * @return UUID анализа или {@code null}, если не удалось извлечь
     */
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
