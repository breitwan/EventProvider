package event.method;

import event.api.EventSubscriber;
import event.api.Priority;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public final class MethodEventSubscriber<E, L> implements EventSubscriber<E> {
    private final Class<? extends E> event;
    private final Priority priority;
    @Getter
    private final L listener;
    private final Agent<E, L> executor;

    public MethodEventSubscriber(Class<? extends E> eventClass, Priority priority, L listener, Agent<E, L> executor) {
        this.event = eventClass;
        this.priority = priority;
        this.listener = listener;
        this.executor = executor;
    }

    @Override
    public void handle(E event) throws Throwable {
        executor.invoke(event, listener);
    }

    @Override
    public Priority priority() {
        return priority;
    }
}