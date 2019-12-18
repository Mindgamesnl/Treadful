package com.craftmend.treadful.scheduler;

import com.craftmend.treadful.Treadful;
import com.craftmend.treadful.scheduler.custom.SchedulerImplementation;
import com.craftmend.treadful.scheduler.provider.SchedulerProvider;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.scheduler.CraftScheduler;
import org.bukkit.craftbukkit.v1_12_R1.scheduler.CraftTask;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class SchedulerFactory {

    public SchedulerImplementation inject() throws NoSuchFieldException, IllegalAccessException {
        CraftScheduler scheduler = (CraftScheduler) Bukkit.getScheduler();

        Treadful.getInstance().initializeProvider();
        SchedulerImplementation copy = new SchedulerImplementation();
        List<String> ignored = Arrays.asList(new String[]{"RECENT_TICKS", "runners", "pending"});

        // apply world fields
        for (Field declaredField : CraftScheduler.class.getDeclaredFields()) {
            declaredField.setAccessible(true);

            Field fieldInCopy = CraftScheduler.class.getDeclaredField(declaredField.getName());
            fieldInCopy.setAccessible(true);

            if(!ignored.contains(declaredField.getName())) {
                fieldInCopy.set(copy, declaredField.get(scheduler));
            }
        }

        Field schedulerField = CraftServer.class.getDeclaredField("scheduler");
        schedulerField.setAccessible(true);

        // move all runners, pending etc to the correct handler
        Field originalPendingField = CraftScheduler.class.getDeclaredField("pending");
        originalPendingField.setAccessible(true);
        PriorityQueue<CraftTask> originalPending = (PriorityQueue<CraftTask>) originalPendingField.get(scheduler);

        SchedulerProvider provider = Treadful.getInstance().getSchedulerProvider();

        for (CraftTask task : originalPending) {
            // get owner
            CraftScheduler env = provider.getScheduler(task.getOwner());

            Field taskPendingField = CraftScheduler.class.getDeclaredField("pending");
            taskPendingField.setAccessible(true);
            PriorityQueue<CraftTask> pending = (PriorityQueue<CraftTask>) taskPendingField.get(env);

            pending.add(task);

            taskPendingField.set(env, pending);
        }

        // and now for the runners
        Field originalRunnersField = CraftScheduler.class.getDeclaredField("runners");
        originalRunnersField.setAccessible(true);
        ConcurrentHashMap<Integer, CraftTask> originalRunners = (ConcurrentHashMap<Integer, CraftTask>) originalRunnersField.get(scheduler);

        for (Map.Entry<Integer, CraftTask> entry : originalRunners.entrySet()) {
            Integer id = entry.getKey();
            CraftTask task = entry.getValue();

            CraftScheduler env = provider.getScheduler(task.getOwner());

            Field taskRunnersField = CraftScheduler.class.getDeclaredField("runners");
            taskRunnersField.setAccessible(true);
            ConcurrentHashMap<Integer, CraftTask> runner = (ConcurrentHashMap<Integer, CraftTask>) taskRunnersField.get(env);

            runner.put(id, task);

            taskRunnersField.set(env, runner);
        }

        // and again, for temp
        Field originalTempField = CraftScheduler.class.getDeclaredField("temp");
        originalTempField.setAccessible(true);
        List<CraftTask> temp = (List<CraftTask>) originalTempField.get(scheduler);

        for (CraftTask task : temp) {
            // get owner
            CraftScheduler env = provider.getScheduler(task.getOwner());

            Field taskTempField = CraftScheduler.class.getDeclaredField("temp");
            taskTempField.setAccessible(true);
            List<CraftTask> pending = (List<CraftTask>) taskTempField.get(env);

            pending.add(task);

            taskTempField.set(env, pending);
        }

        schedulerField.set(Bukkit.getServer(), copy);

        return copy;
    }

}
