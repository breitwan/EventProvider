package event;

import event.api.EventProvider;
import event.api.EventSubscriber;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Стандартный провайдер событий.
 *
 * @param <E> тип события.
 */
public class DefaultEventProvider<E> implements EventProvider<E> {
    protected final Class<E> type;
    protected final SubscriberRegistry<E> registry = new SubscriberRegistry<>();

    public DefaultEventProvider(Class<E> type) {
        Objects.requireNonNull(type, "type");
        this.type = type;
    }

    @Override
    public <T extends E> void register(Class<T> event, EventSubscriber<? super T> subscriber) {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(subscriber, "subscriber");
        registry.register(event, subscriber);
    }

    @Override
    public void publish(E event) {
        Objects.requireNonNull(event, "event");
        //noinspection rawtypes
        for (EventSubscriber subscriber : registry.subscribers(event.getClass())) {
            try {
                subscriber.handle(event);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unregister(EventSubscriber<?> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");
        registry.unregister(subscriber);
    }

    @Override
    public void unregister(Predicate<EventSubscriber<?>> predicate) {
        registry.unregisterMatching(predicate);
    }

    @Override
    public void unregisterAll() {
        registry.unregisterAll();
    }
}