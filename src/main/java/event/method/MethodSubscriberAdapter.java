package event.method;

import event.api.EventProvider;
import event.api.EventSubscriber;
import event.api.method.Subscribe;
import event.method.exception.SubscriberGenerationException;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public final class MethodSubscriberAdapter<L, E> {
    private final Class<E> type;
    private final EventProvider<E> provider;
    private final Agent.Factory<E, L> factory;

    public MethodSubscriberAdapter(Class<E> type, EventProvider<E> provider, Agent.Factory<E, L> factory) {
        this.type = type;
        this.provider = provider;
        this.factory = factory;
    }

    public void register(L listener) {
        adapt(listener, provider::register);
    }

    public void unregister(L listener) {
        provider.unregister(sub -> sub instanceof MethodEventSubscriber && ((MethodEventSubscriber) sub).getListener() == listener);
    }

    public void adapt(L listener, BiConsumer<Class<? extends E>, EventSubscriber<E>> consumer) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Subscribe.class)) {
                continue;
            }

            if (method.getParameterCount() != 1) {
                throw new SubscriberGenerationException("Подписчик '" + method + "' должен иметь только один параметр");
            }

            Class<?> methodParameterType = method.getParameterTypes()[0];
            if (!type.isAssignableFrom(methodParameterType)) {
                throw new SubscriberGenerationException("Параметр метода '" + methodParameterType + "' на наследует событие типа '" + type + "'");
            }

            Agent<E, L> executor;
            try {
                executor = factory.create(listener, method);
            } catch (Throwable e) {
                throw new SubscriberGenerationException("Неудача во время создания посредника для метода '" + method + '\'', e);
            }

            Class<? extends E> clazz = (Class<? extends E>) methodParameterType;
            consumer.accept(clazz, new MethodEventSubscriber<>(clazz, method.getAnnotation(Subscribe.class).priority(), listener, executor));
        }
    }
}