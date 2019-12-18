package com.craftmend.treadful.world;

import com.craftmend.treadful.world.interfaces.HotSwappable;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class WorldFactory {

    public WorldServer overwriteWorld(CraftWorld world, Class type) {
        try {
            return copy(world, type);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private WorldServer copy(CraftWorld craftWorld, Class target) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
        WorldServer handle = craftWorld.getHandle();

        WorldServer copy = (WorldServer) create(target, Object.class);
        Set<String> ignored = new HashSet<>(Arrays.asList(new String[]{"a"}));

        // apply world fields
        for (Field declaredField : World.class.getDeclaredFields()) {
            declaredField.setAccessible(true);

            Field fieldInCopy = World.class.getDeclaredField(declaredField.getName());
            fieldInCopy.setAccessible(true);

            if(!ignored.contains(declaredField.getName())) {
                fieldInCopy.set(copy, declaredField.get(handle));
            }
        }

        // apply worldserver fields
        for (Field declaredField : WorldServer.class.getDeclaredFields()) {
            declaredField.setAccessible(true);

            Field fieldInCopy = WorldServer.class.getDeclaredField(declaredField.getName());
            fieldInCopy.setAccessible(true);

            if(!ignored.contains(declaredField.getName())) {
                fieldInCopy.set(copy, declaredField.get(handle));
            }
        }

        Field serverField = CraftWorld.class.getDeclaredField("world");
        serverField.setAccessible(true);

        // override in mc server
        MinecraftServer minecraftServer = MinecraftServer.getServer();
        minecraftServer.worlds.remove(handle);
        minecraftServer.worlds.add(copy);

        serverField.set(craftWorld, copy);

        if (copy instanceof HotSwappable) {
            HotSwappable hs = (HotSwappable) copy;
            hs.onSwap();
        }

        return copy;
    }

    private <T> T create(Class<T> clazz,
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
}
