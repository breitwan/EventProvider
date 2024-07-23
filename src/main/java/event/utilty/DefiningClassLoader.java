package event.utilty;

/**
 * Для загрузки собственных классов:
 * у {@link ClassLoader} метод {@link ClassLoader#defineClass(String, byte[], int, int)} является protected.
 */
public final class DefiningClassLoader extends ClassLoader {
    public DefiningClassLoader(ClassLoader parent) {
        super(parent);
    }

    public <T> Class<T> defineClass(String name, byte[] bytes) {
        return (Class<T>) defineClass(name, bytes, 0, bytes.length);
    }
}