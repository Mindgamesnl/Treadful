package com.craftmend.treadful.world.wrappers;

import com.craftmend.treadful.world.interfaces.HotSwappable;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class CustomWorldServer extends WorldServer implements HotSwappable {

    private BlockingQueue<Runnable> blockingQueue;

    private Instant lastTick = Instant.now();

    // replacement fields
    protected SpawnerCreature spawnerCreature;

    // reflection fields
    private Field playerChunkMapField;
    private Field portalTravelAgent;
    private Method soundHandler;
    private Method worldTick;
    private Field playerChunkI;

    public CustomWorldServer(MinecraftServer minecraftserver, IDataManager idatamanager, WorldData worlddata, int i, MethodProfiler methodprofiler, World.Environment env, ChunkGenerator gen) {
        super(minecraftserver, idatamanager, worlddata, i, methodprofiler, env, gen);
    }

    @Override
    public void onSwap() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException {
        lastTick = Instant.now();
        blockingQueue = new LinkedBlockingDeque<>();
        playerChunkMapField = WorldServer.class.getDeclaredField("manager");
        portalTravelAgent = WorldServer.class.getDeclaredField("portalTravelAgent");
        soundHandler = WorldServer.class.getDeclaredMethod("aq");
        worldTick = net.minecraft.server.v1_12_R1.World.class.getDeclaredMethod("doTick");
        Field spawner = WorldServer.class.getDeclaredField("spawnerCreature");
        playerChunkI = PlayerChunkMap.class.getDeclaredField("i");

        soundHandler.setAccessible(true);
        portalTravelAgent.setAccessible(true);
        playerChunkMapField.setAccessible(true);
        worldTick.setAccessible(true);
        spawner.setAccessible(true);
        playerChunkI.setAccessible(true);

        spawnerCreature = (SpawnerCreature) spawner.get(this);
    }

    public List<PlayerChunk> getPlayerChunkI() throws IllegalAccessException {
        return (List<PlayerChunk>) playerChunkI.get(getManager());
    }

    public Instant getLastTick() {
        return lastTick;
    }

    public void doI() {
        this.i();
    }

    public void setL(int l2) {
        this.l = l2;
    }

    public void doF() {
        this.f();
    }

    public PersistentVillage getVillages() {
        return villages;
    }

    public VillageSiege getSiegeManager() {
        return siegeManager;
    }

    public SpawnerCreature getSpawnerCreature() {
        return spawnerCreature;
    }

    public void scheduleSync(Runnable runnable) {
        blockingQueue.add(runnable);
    }

    public void keepAlive() {
        lastTick = Instant.now();
    }

    public void tickScheduledInterceptions() {
        if (blockingQueue.size() == 0) return;
        for (Runnable runnable : blockingQueue) {
            runnable.run();
        }
        blockingQueue.clear();
    }

    public BlockPosition doA(BlockPosition bp) {
        return this.a(bp);
    }

    public int getL() {
        return this.l;
    }

    public void tickWorld() {
        boolean flag = this.getGameRules().getBoolean("doWeatherCycle");
        int idx;
        if (flag) {
            idx = this.worldData.z();
            if (idx > 0) {
                --idx;
                this.worldData.i(idx);
                this.worldData.setThunderDuration(this.worldData.isThundering() ? 1 : 2);
                this.worldData.setWeatherDuration(this.worldData.hasStorm() ? 1 : 2);
            }

            int j = this.worldData.getThunderDuration();
            if (j <= 0) {
                if (this.worldData.isThundering()) {
                    this.worldData.setThunderDuration(this.random.nextInt(12000) + 3600);
                } else {
                    this.worldData.setThunderDuration(this.random.nextInt(168000) + 12000);
                }
            } else {
                --j;
                this.worldData.setThunderDuration(j);
                if (j <= 0) {
                    this.worldData.setThundering(!this.worldData.isThundering());
                }
            }

            int k = this.worldData.getWeatherDuration();
            if (k <= 0) {
                if (this.worldData.hasStorm()) {
                    this.worldData.setWeatherDuration(this.random.nextInt(12000) + 12000);
                } else {
                    this.worldData.setWeatherDuration(this.random.nextInt(168000) + 12000);
                }
            } else {
                --k;
                this.worldData.setWeatherDuration(k);
                if (k <= 0) {
                    this.worldData.setStorm(!this.worldData.hasStorm());
                }
            }
        }

        this.p = this.q;
        if (this.worldData.isThundering()) {
            this.q = (float)((double)this.q + 0.01D);
        } else {
            this.q = (float)((double)this.q - 0.01D);
        }

        this.q = MathHelper.a(this.q, 0.0F, 1.0F);
        this.n = this.o;
        if (this.worldData.hasStorm()) {
            this.o = (float)((double)this.o + 0.01D);
        } else {
            this.o = (float)((double)this.o - 0.01D);
        }

        this.o = MathHelper.a(this.o, 0.0F, 1.0F);

        for(idx = 0; idx < this.players.size(); ++idx) {
            if (((EntityPlayer)this.players.get(idx)).world == this) {
                ((EntityPlayer)this.players.get(idx)).tickWeather();
            }
        }
    }

    public void tickSounds() {
        try {
            soundHandler.invoke(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public PortalTravelAgent getTravelAgent() {
        try {
            return (PortalTravelAgent) portalTravelAgent.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PlayerChunkMap getManager() {
        try {
            return (PlayerChunkMap) playerChunkMapField.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
