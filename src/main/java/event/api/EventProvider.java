package event.api;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Провайдер.
 *
 * @param <E> тип события.
 */
public interface EventProvider<E> {
    /**
     * Зарегистрировать подписчика.
     *
     * @param event      событие
     * @param subscriber подписчик
     */
    <T extends E> void register(Class<T> event, EventSubscriber<? super T> subscriber);

    /**
     * Создать и зарегистрировать подписчика.
     *
     * @param event    событие
     * @param priority приоритет подписчика
     * @param consumer логика подписчика
     * @return подписчик
     */
    default <T extends E> EventSubscriber<? super T> register(Class<T> event, @Nullable Priority priority, Consumer<? super T> consumer) {
        EventSubscriber<? super T> subscriber = new EventSubscriber<T>() {
            @Override
            public void handle(T event) {
                consumer.accept(event);
            }

            @Override
            public Priority priority() {
                return priority != null ? priority : Priority.NORMAL;
            }
        };
        register(event, subscriber);
        return subscriber;
    }

    /**
     * Опубликовать событие для всех зарегистрированных подписчиков.
     *
     * @param event событие
     */
    void publish(E event);

    /**
     * Разрегистрировать подписчика.
     *
     * @param subscriber подписчик
     */
    void unregister(EventSubscriber<?> subscriber);

    /**
     * Разрегистрировать всех подписчиков, удовлетворяющих условие.
     *
     * @param predicate условие
     */
    void unregister(Predicate<EventSubscriber<?>> predicate);

    /**
     * Разрегистрировать всех подписчиков.
     */
    void unregisterAll();
}