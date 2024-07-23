package event.api.method;

import event.api.Priority;

import java.lang.annotation.*;

/**
 * Кандидат на статус подписчика.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
    /**
     * Приоритет подписчика.
     */
    Priority priority() default Priority.NORMAL;
}