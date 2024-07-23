package event.api.method;

import event.api.EventProvider;

/**
 * Провайдер событий, способный регистрировать методы с аннотацией {@link Subscribe}.
 *
 * @param <L> тип слушателя.
 * @param <E> тип события.
 */
public interface MethodEventProvider<L, E> extends EventProvider<E> {
    /**
     * Зарегистрировать подписчиков слушателя.
     */
    void register(L listener);

    /**
     * Разрегистрировать подписчиков слушателя.
     */
    void unregister(L listener);
}