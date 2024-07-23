package event.method;

import java.lang.reflect.Method;

/**
 * Посредник при вызове подписчиков, зарегистрированных аннотацией {@link event.api.method.Subscribe}.
 *
 * @param <L> тип слушателя.
 * @param <E> тип события.
 */
@FunctionalInterface
public interface Agent<L, E> {
    /**
     * Вызвать обработку события
     */
    void invoke(L listener, E event) throws Throwable;

    /**
     * Фабрика посредников
     *
     * @param <L> тип слушателя
     * @param <E> тип события
     */
    @FunctionalInterface
    interface Factory<L, E> {
        Agent<L, E> create(Object object, Method method) throws Throwable;
    }
}