package event;

import event.api.Priority;
import event.api.method.MethodEventProvider;
import event.api.method.Subscribe;
import event.method.DefaultMethodEventProvider;

public final class Test {
    public static void main(String... args) {
        MethodEventProvider<Object, Object> provider = DefaultMethodEventProvider.COMMON_EVENT_PROVIDER;

        Listener listener = new Listener();
        provider.register(listener);

        Listener listener2 = new Listener();
        provider.register(listener2);

        provider.publish(new TestEvent());

        provider.unregister(listener);
        System.out.println("unregistered");

        provider.publish(new TestEvent());
    }

    public static class Listener {
        @Subscribe(priority = Priority.NORMAL)
        public void listen(TestEvent event) {
            System.out.println("Consumed: " + event.lol);
        }
    }

    public static class TestEvent {
        public int lol = 5;
    }
}