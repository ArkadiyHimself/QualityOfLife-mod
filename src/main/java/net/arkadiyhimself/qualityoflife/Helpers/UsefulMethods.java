package net.arkadiyhimself.qualityoflife.Helpers;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Random;

public class UsefulMethods {
    public static void spawnBonemealParticles(Level level, BlockPos pos) {
        Random RANDOM = new Random();
        level.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
        for (int i = 0; i < 5; ++i) {
            double d2 = RANDOM.nextGaussian() * 0.02D;
            double d3 = RANDOM.nextGaussian() * 0.02D;
            double d4 = RANDOM.nextGaussian() * 0.02D;
            double d6 = pos.getX() + RANDOM.nextDouble();
            double d7 = pos.getY() + RANDOM.nextDouble();
            double d8 = pos.getZ() + RANDOM.nextDouble();

            level.addParticle(ParticleTypes.HAPPY_VILLAGER, d6, d7, d8, d2, d3, d4);
        }
    }

    public static BlockPos highestGrow(Level world, BlockPos pos, Block block, boolean goUp) {
        while (true) {
            world.getBlockState(pos);
            if (world.getBlockState(pos).getBlock() == block) {
                BlockPos nextUp = goUp ? pos.above() : pos.below();

                world.getBlockState(nextUp);
                if (world.getBlockState(nextUp).getBlock() != block)
                    return pos;
                else {
                    pos = nextUp;
                }
            } else {
                return pos;
            }
        }
    }
    public static final Map<Block, Block> COBBLES = (new ImmutableMap.Builder<Block, Block>())
            .put(Blocks.STONE, Blocks.COBBLESTONE)
            .put(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE)
            .put(Blocks.POLISHED_ANDESITE, Blocks.ANDESITE)
            .put(Blocks.POLISHED_DIORITE, Blocks.DIORITE)
            .put(Blocks.POLISHED_GRANITE, Blocks.GRANITE)
            .put(Blocks.POLISHED_BASALT, Blocks.BASALT)
            .put(Blocks.POLISHED_BLACKSTONE, Blocks.BLACKSTONE)
            .put(Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES)
            .put(Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS)
            .put(Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS)
            .put(Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS)
            .put(Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)
            .build();
    public static class geometry {
        static Random random = new Random();
        public static void randomSidesOfSquare(Vec3 center, double halfside) {
            randomSidesOfSquare(center.x, center.y, center.z, halfside);
        }
        public static void randomSidesOfSquare(BlockPos center, double halfside) {
            randomSidesOfSquare(center.getX(), center.getY(), center.getZ(), halfside);
        }
        public static Vec3 randomSidesOfSquare(double x, double y, double z, double halfside) {
            switch (random.nextInt(1, 7)) {
                case 1: {
                    y += halfside;
                    x += random.nextDouble(-halfside, halfside);
                    z += random.nextDouble(-halfside, halfside);
                    break;
                }
                case 2: {
                    y -= halfside;
                    x += random.nextDouble(-halfside, halfside);
                    z += random.nextDouble(-halfside, halfside);
                    break;
                }
                case 3: {
                    x += halfside;
                    y += random.nextDouble(-halfside, halfside);
                    z += random.nextDouble(-halfside, halfside);
                    break;
                }
                case 4: {
                    x -= halfside;
                    y += random.nextDouble(-halfside, halfside);
                    z += random.nextDouble(-halfside, halfside);
                    break;
                }
                case 5: {
                    z += halfside;
                    x += random.nextDouble(-halfside, halfside);
                    y += random.nextDouble(-halfside, halfside);
                    break;
                }
                case 6: {
                    z -= halfside;
                    x += random.nextDouble(-halfside, halfside);
                    y += random.nextDouble(-halfside, halfside);
                    break;
                }
            }
            return new Vec3(x, y, z);
        }
    }
}
