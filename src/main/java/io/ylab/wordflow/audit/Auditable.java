package io.ylab.wordflow.audit;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для маркировки методов, подлежащих аудиту.
 * Используется совместно с {@link AuditAspect} для автоматического логирования действий пользователя.
 *
 * <p>Применяется к методам контроллеров или сервисов, вызов которых должен быть зафиксирован
 * в таблице аудита. Аспект перехватывает выполнение метода и сохраняет запись с указанным действием,
 * параметрами и идентификатором анализа (если применимо).</p>
 *
 * @see AuditAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    String action();
}
