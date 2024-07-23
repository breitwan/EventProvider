package event.method;

import event.DefaultEventProvider;
import event.api.method.MethodEventProvider;
import event.method.asm.ASMAgentFactory;

import java.util.Objects;

/**
 * Стандартный провайдер событий, реализация {@link MethodEventProvider}.
 *
 * @param <E> тип события.
 * @param <L> тип слушателя.
 */
public final class DefaultMethodEventProvider<L, E> extends DefaultEventProvider<E> implements MethodEventProvider<L, E> {
    public static final MethodEventProvider<Object, Object> COMMON_EVENT_PROVIDER = new DefaultMethodEventProvider<>(Object.class);

    private final MethodSubscriberAdapter<L, E> adapter = new MethodSubscriberAdapter<>(type, this, new ASMAgentFactory<>(getClass().getClassLoader()));

    public DefaultMethodEventProvider(Class<E> type) {
        super(Objects.requireNonNull(type, "type"));
    }

    @Override
    public void register(L listener) {
        Objects.requireNonNull(listener, "listener");
        adapter.register(listener);
    }

    @Override
    public void unregister(L listener) {
        Objects.requireNonNull(listener, "listener");
        adapter.unregister(listener);
    }
}