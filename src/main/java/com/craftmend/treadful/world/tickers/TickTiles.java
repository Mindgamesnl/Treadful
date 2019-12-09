package com.craftmend.treadful.world.tickers;

import com.craftmend.treadful.world.interfaces.InjectedTickable;
import com.craftmend.treadful.world.wrappers.CustomWorldServer;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Iterator;
import java.util.List;

public class TickTiles implements InjectedTickable {
    @Override
    public void tick(CustomWorldServer instance) {
        try {
            tickTiles(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void tickTiles(CustomWorldServer instance) throws IllegalAccessException {
        instance.doI();
        if (instance.worldData.getType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            Iterator<Chunk> iterator = instance.getManager().b();

            while (iterator.hasNext()) {
                ((Chunk)iterator.next()).b(false);
            }
        } else {

            int i = instance.getGameRules().c("randomTickSpeed");
            boolean flag = instance.isRaining();
            boolean flag1 = instance.X();

            instance.methodProfiler.a("pollingChunks");

            List<PlayerChunk> chunkCollection = instance.getPlayerChunkI();
            int size = chunkCollection.size() - 1;

            for (int chunkId = 0; chunkId < size; chunkId++) {
                if (chunkId > chunkCollection.size() - 1) continue;
                Chunk chunk = chunkCollection.get(chunkId).chunk;

                instance.methodProfiler.a("getChunk");

                if (chunk == null) continue;

                int j = chunk.locX * 16;
                int k = chunk.locZ * 16;

                instance.methodProfiler.c("checkNextLight");
                chunk.n();
                instance.methodProfiler.c("tickChunk");
                chunk.b(false);
                if (chunk.areNeighborsLoaded(1)) {
                    instance.methodProfiler.c("thunder");



                    if (flag && flag1 && instance.random.nextInt(100000) == 0) {
                        instance.setL(instance.getL() * 3 + 1013904223);
                        int l = instance.getL() >> 2;
                        BlockPosition blockposition = instance.doA(new BlockPosition(j + (l & 0xF), 0, k + (l >> 8 & 0xF)));
                        if (instance.isRainingAt(blockposition)) {
                            DifficultyDamageScaler difficultydamagescaler = instance.D(blockposition);

                            if (instance.getGameRules().getBoolean("doMobSpawning") && instance.random.nextDouble() < difficultydamagescaler.b() * 0.01D) {
                                EntityHorseSkeleton entityhorseskeleton = new EntityHorseSkeleton(instance);

                                entityhorseskeleton.p(true);
                                entityhorseskeleton.setAgeRaw(0);
                                entityhorseskeleton.setPosition(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                                instance.addEntity(entityhorseskeleton, CreatureSpawnEvent.SpawnReason.LIGHTNING);
                                instance.strikeLightning(new EntityLightning(instance, blockposition.getX(), blockposition.getY(), blockposition.getZ(), true));
                            } else {
                                instance.strikeLightning(new EntityLightning(instance, blockposition.getX(), blockposition.getY(), blockposition.getZ(), false));
                            }
                        }
                    }

                    instance.methodProfiler.c("iceandsnow");
                    if (instance.random.nextInt(16) == 0) {
                        instance.setL(instance.getL() * 3 + 1013904223);
                        int l = instance.getL() >> 2;
                        BlockPosition blockposition = instance.p(new BlockPosition(j + (l & 0xF), 0, k + (l >> 8 & 0xF)));
                        BlockPosition blockposition1 = blockposition.down();

                        if (instance.v(blockposition1)) {
                            CraftEventFactory.handleBlockFormEvent(instance, blockposition1, Blocks.ICE.getBlockData(), null);
                        }

                        if (flag && instance.f(blockposition, true)) {
                            CraftEventFactory.handleBlockFormEvent(instance, blockposition, Blocks.SNOW_LAYER.getBlockData(), null);
                        }

                        if (flag && instance.getBiome(blockposition1).d()) {
                            instance.getType(blockposition1).getBlock().h(instance, blockposition1);
                        }
                    }

                    instance.methodProfiler.c("tickBlocks");
                    if (i > 0) {
                        ChunkSection[] achunksection = chunk.getSections();
                        int i1 = achunksection.length;

                        for (int j1 = 0; j1 < i1; j1++) {
                            ChunkSection chunksection = achunksection[j1];

                            if (chunksection != Chunk.a && chunksection.shouldTick()) {
                                for (int k1 = 0; k1 < i; k1++) {
                                    instance.setL(instance.getL() * 3 + 1013904223);
                                    int l1 = instance.getL() >> 2;
                                    int i2 = l1 & 0xF;
                                    int j2 = l1 >> 8 & 0xF;
                                    int k2 = l1 >> 16 & 0xF;
                                    IBlockData iblockdata = chunksection.getType(i2, k2, j2);
                                    Block block = iblockdata.getBlock();

                                    instance.methodProfiler.a("randomTick");
                                    if (block.isTicking()) {
                                        instance.scheduleSync(() -> {
                                            block.a(instance, new BlockPosition(i2 + j, k2 + chunksection.getYPosition(), j2 + k), iblockdata, instance.random);
                                        });
                                    }
                                    instance.methodProfiler.b();
                                }
                            }
                        }
                    }
                }
            }
            instance.methodProfiler.b();
        }
    }
}
