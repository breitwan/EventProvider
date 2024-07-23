package event.method.asm;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import event.method.Agent;
import event.utilty.DefiningClassLoader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

public final class ASMAgentFactory<L, E> implements Agent.Factory<L, E> {
    private static final String PACKAGE = "event.method.asm.generated";

    private static final String OBJECT_INTERNAL_NAME = "java/lang/Object";
    private static final String[] AGENT_INTERNAL_NAME = new String[]{Type.getInternalName(Agent.class)};

    private static final String AGENT_METHOD_DESCRIPTOR = "(Ljava/lang/Object;Ljava/lang/Object;)V";

    private final DefiningClassLoader classloader;
    private final String session = UUID.randomUUID().toString().substring(36 - 8);

    private final AtomicInteger id = new AtomicInteger();
    private final LoadingCache<Method, Class<? extends Agent<L, E>>> cache;

    public ASMAgentFactory(ClassLoader parent) {
        this.classloader = new DefiningClassLoader(parent);
        this.cache = CacheBuilder.newBuilder()
                .weakValues()
                .initialCapacity(8)
                .build(CacheLoader.from(method -> {
                    Objects.requireNonNull(method, "method");

                    Class<?> listener = method.getDeclaringClass();
                    String listenerName = Type.getInternalName(listener);

                    Class<?> event = method.getParameterTypes()[0];
                    String eventName = Type.getInternalName(event);

                    String className = String.format("%s.%s.%s$%s.%s$%d",
                            PACKAGE, session, listener.getSimpleName(), method.getName(), event.getSimpleName(), id.incrementAndGet());

                    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                    cw.visit(V1_8, ACC_PUBLIC | ACC_FINAL, className.replace('.', '/'), null, OBJECT_INTERNAL_NAME, AGENT_INTERNAL_NAME);

                    MethodVisitor mv;

                    mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                    mv.visitCode();

                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKESPECIAL, OBJECT_INTERNAL_NAME, "<init>", "()V", false);

                    mv.visitInsn(RETURN);

                    mv.visitMaxs(0, 0);
                    mv.visitEnd();

                    mv = cw.visitMethod(ACC_PUBLIC, "invoke", AGENT_METHOD_DESCRIPTOR, null, null);
                    mv.visitCode();

                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitTypeInsn(CHECKCAST, eventName);
                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitTypeInsn(CHECKCAST, listenerName);

                    mv.visitInsn(SWAP);
                    mv.visitMethodInsn(INVOKEVIRTUAL, listenerName, method.getName(), Type.getMethodDescriptor(method), false);

                    mv.visitInsn(RETURN);

                    mv.visitMaxs(0, 0);
                    mv.visitEnd();

                    cw.visitEnd();

                    return classloader.defineClass(className, cw.toByteArray());
                }));
    }

    public Agent<L, E> create(Object object, Method method) throws IllegalAccessException, InstantiationException {
        if (!Modifier.isPublic(object.getClass().getModifiers())) {
            throw new IllegalArgumentException("Слушатель '" + object.getClass().getName() + "' должен быть общедоступным");
        }
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException("Подписчик '" + method + "' должен быть общедоступным");
        }
        return cache.getUnchecked(method).newInstance();
    }
}