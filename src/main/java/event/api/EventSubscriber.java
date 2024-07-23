package event.api;

/**
 * Подписчик.
 *
 * @param <E> тип события.
 */
@FunctionalInterface
public interface EventSubscriber<E> {
    /**
     * Вызвать подписчика на обработку события.
     */
    void handle(E event) throws Throwable;

    /**
     * Проиоритет подписчика.
     */
    default Priority priority() {
        return Priority.NORMAL;
    }
}