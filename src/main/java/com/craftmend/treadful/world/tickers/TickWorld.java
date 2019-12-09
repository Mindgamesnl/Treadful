package com.craftmend.treadful.world.tickers;

import com.craftmend.treadful.world.interfaces.InjectedTickable;
import com.craftmend.treadful.world.wrappers.CustomWorldServer;
import net.minecraft.server.v1_12_R1.EnumDifficulty;
import net.minecraft.server.v1_12_R1.WorldServer;
import net.minecraft.server.v1_12_R1.WorldType;

public class TickWorld implements InjectedTickable {

    @Override
    public void tick(CustomWorldServer instance) {
        instance.tickWorld();

        if (instance.getWorldData().isHardcore() && instance.getDifficulty() != EnumDifficulty.HARD) {
            instance.getWorldData().setDifficulty(EnumDifficulty.HARD);
        }

        instance.worldProvider.k().b();
        long time;
        if (instance.everyoneDeeplySleeping()) {
            if (instance.getGameRules().getBoolean("doDaylightCycle")) {
                time = instance.worldData.getDayTime() + 24000L;
                instance.worldData.setDayTime(time - time % 24000L);
            }

            instance.doF();
        }

        instance.methodProfiler.a("mobSpawner");
        time = instance.worldData.getTime();
        if (instance.getGameRules().getBoolean("doMobSpawning") && instance.worldData.getType() != WorldType.DEBUG_ALL_BLOCK_STATES && (instance.allowMonsters || instance.allowAnimals) && instance instanceof WorldServer && instance.players.size() > 0) {
            long finalTime = time;
            instance.scheduleSync(() -> {
                instance.timings.mobSpawn.startTiming();
                instance.getSpawnerCreature().a(instance, instance.allowMonsters && instance.ticksPerMonsterSpawns != 0L && finalTime % instance.ticksPerMonsterSpawns == 0L, instance.allowAnimals && instance.ticksPerAnimalSpawns != 0L && finalTime % instance.ticksPerAnimalSpawns == 0L, instance.worldData.getTime() % 400L == 0L);
                instance.timings.mobSpawn.stopTiming();
            });
        }
    }
}
