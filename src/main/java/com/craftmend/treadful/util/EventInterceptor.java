package com.craftmend.treadful.util;

import com.craftmend.treadful.Treadful;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

public class EventInterceptor extends HandlerList {

    private List<Consumer<Event>> preExecutors = new ArrayList<>();
    private List<Consumer<Event>> postExecutors = new ArrayList<>();
    private BiConsumer<Event, Runnable> middleware = (e,r) -> r.run();

    public static EventInterceptor create(Class<? extends Event> event) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        // get the current handler list, maybe  it already is intercepted, then we can just use that one
        Event instance = create(event, Object.class);
        if (instance.getHandlers() instanceof  EventInterceptor) return (EventInterceptor) instance.getHandlers();

        Field field = null;
        for (Field declaredField : event.getDeclaredFields()) {
            if (declaredField.getType() == EventInterceptor.class) return (EventInterceptor) declaredField.get(null);
            if (declaredField.getType() == HandlerList.class) field = declaredField;
        }

        if (field == null) {
            throw new IllegalStateException("Handler not found");
        }

        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        EventInterceptor interceptor = new EventInterceptor();

        field.set(null, interceptor);

        return interceptor;
    }

    public void reset() {
        middleware = (e,r) -> r.run();
        postExecutors = new ArrayList<>();
        preExecutors = new ArrayList<>();
    }

    public void setMiddleware(BiConsumer<Event, Runnable> middleware) {
        this.middleware = middleware;
    }

    public void addPreExecutor(Consumer<Event> preExecutor) {
        this.preExecutors.add(preExecutor);
    }

    public void addPostExecutor(Consumer<Event> postExecutor) {
        this.postExecutors.add(postExecutor);
    }

    @Override
    public RegisteredListener[] getRegisteredListeners() {

        RegisteredListener preExecutor = new RegisteredListener(null, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event event) throws EventException {
                for (Consumer<Event> preExecutor : preExecutors) {
                    preExecutor.accept(event);
                }
            }
        }, EventPriority.LOWEST, Treadful.getInstance(), true);

        RegisteredListener postExecutor = new RegisteredListener(null, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event event) throws EventException {
                for (Consumer<Event> postExecutor : postExecutors) {
                    postExecutor.accept(event);
                }
            }
        }, EventPriority.LOWEST, Treadful.getInstance(), true);

        List<RegisteredListener> executorList = new ArrayList<>();

        executorList.add(preExecutor);

        executorList.add(new RegisteredListener(null, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event event) throws EventException {

                middleware.accept(event, () -> {
                    callActualEvents(event);
                });

            }
        }, EventPriority.LOWEST, Treadful.getInstance(), true));

        executorList.add(postExecutor);


        return executorList.toArray(new RegisteredListener[executorList.size()]);
    }

    private void callActualEvents(Event event) {
        for (RegisteredListener listener : super.getRegisteredListeners()) {
            try {
                listener.callEvent(event);
            } catch (AuthorNagException var10) {
                Plugin plugin = listener.getPlugin();
                if (plugin.isNaggable()) {
                    plugin.setNaggable(false);
                    Bukkit.getServer().getLogger().log(Level.SEVERE, String.format("Nag author(s): '%s' of '%s' about the following: %s", plugin.getDescription().getAuthors(), plugin.getDescription().getFullName(), var10.getMessage()));
                }
            } catch (Throwable var11) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + listener.getPlugin().getDescription().getFullName(), var11);
            }
        }
    }

    private static  <T> T create(Class<T> clazz,
                         Class<? super T> parent) {
        try {
            ReflectionFactory rf =
                    ReflectionFactory.getReflectionFactory();
            Constructor objDef = parent.getDeclaredConstructor();
            Constructor intConstr = rf.newConstructorForSerialization(
                    clazz, objDef
            );
            return clazz.cast(intConstr.newInstance());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create object", e);
        }
    }

    private RegisteredListener[] pushToBeginning(RegisteredListener[] elements, RegisteredListener element) {
        RegisteredListener[] newArray = Arrays.copyOf(elements, elements.length + 1);
        newArray[0] = element;
        System.arraycopy(elements, 0, newArray, 1, elements.length);
        return newArray;
    }

    private RegisteredListener[] pushToEnd(RegisteredListener[] elements, RegisteredListener element) {
        RegisteredListener[] result = Arrays.copyOf(elements, elements.length + 1);
        result[result.length - 1] = element;
        return result;
    }


}
