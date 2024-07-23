package event;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import event.api.EventSubscriber;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Реестр подписчиков.
 *
 * @param <E> тип события.
 */
public final class SubscriberRegistry<E> {
    private static final LoadingCache<Class<?>, Set<Class<?>>> CLASS_HIERARCHY = CacheBuilder.newBuilder()
            .weakKeys()
            .build(CacheLoader.from(key -> (Set<Class<?>>) TypeToken.of(key).getTypes().rawTypes()));

    private final LoadingCache<Class<?>, List<EventSubscriber<?>>> cache = CacheBuilder.newBuilder()
            .initialCapacity(85)
            .build(CacheLoader.from(eventClass -> {
                List<EventSubscriber<?>> subscribers = new ArrayList<>();
                Set<? extends Class<?>> types = CLASS_HIERARCHY.getUnchecked(eventClass);
                assert types != null;
                synchronized (this.lock) {
                    for (Class<?> type : types) {
                        subscribers.addAll(this.subscribers.get(type));
                    }
                }
                subscribers.sort(Comparator.comparingInt(s -> s.priority().ordinal()));
                return subscribers;
            }));
    private final SetMultimap<Class<?>, EventSubscriber<?>> subscribers = LinkedHashMultimap.create();
    private final Object lock = new Object();

    public <T extends E> void register(Class<T> clazz, EventSubscriber<? super T> subscriber) {
        synchronized (lock) {
            subscribers.put(clazz, subscriber);
            cache.invalidateAll();
        }
    }

    public void unregister(EventSubscriber<?> subscriber) {
        this.unregisterMatching(sub -> sub.equals(subscriber));
    }

    public void unregisterMatching(Predicate<EventSubscriber<?>> predicate) {
        synchronized (lock) {
            boolean dirty = subscribers.values().removeIf(predicate);
            if (dirty) {
                cache.invalidateAll();
            }
        }
    }

    public void unregisterAll() {
        synchronized (lock) {
            subscribers.clear();
            cache.invalidateAll();
        }
    }

    public List<EventSubscriber<?>> subscribers(Class<?> clazz) {
        return cache.getUnchecked(clazz);
    }
}