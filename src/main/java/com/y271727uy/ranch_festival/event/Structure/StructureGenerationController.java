package com.y271727uy.ranch_festival.event.Structure;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import com.y271727uy.ranch_festival.dimension.DimensionDefinition;
import com.y271727uy.ranch_festival.dimension.DimensionRegistry;
import com.y271727uy.ranch_festival.season.SeasonAccessChecker;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class StructureGenerationController {
    private static final String DATA_NAME = RanchFestivalMod.MODID + "_fixed_structure_spawn_test";

    private StructureGenerationController() {
    }

    public static void handleLevelLoad(ServerLevel serverLevel) {
        DimensionDefinition definition = DimensionRegistry.findByDimension(serverLevel.dimension());
        if (definition == null || definition.getStructureAxis() == null) {
            return;
        }

        StructureSpawnSavedData data = serverLevel.getDataStorage().computeIfAbsent(
                StructureSpawnSavedData::load,
                StructureSpawnSavedData::new,
                DATA_NAME
        );

        BlockPos origin = definition.getStructureAxis();
        if (Boolean.TRUE.equals(definition.getAutoReset()) && data.generated && !SeasonAccessChecker.isDimensionAccessAllowed(serverLevel, definition)) {
            clearArea(serverLevel, origin.offset(-3, 0, -3), origin.offset(3, 5, 3));
            data.generated = false;
            data.setDirty();
            RanchFestivalMod.LOGGER.info("Auto-reset fixed structure in {} at {}", describeDimension(definition), origin);
            return;
        }

        if (data.generated) {
            return;
        }

        clearArea(serverLevel, origin.offset(-3, 0, -3), origin.offset(3, 5, 3));

        if (!StructureTemplatePlacer.tryPlace(serverLevel, definition)) {
            generateFallbackStructure(serverLevel, origin);
        }

        data.generated = true;
        data.setDirty();
        RanchFestivalMod.LOGGER.info("Generated fixed structure in {} at {}", describeDimension(definition), origin);
    }

    private static void generateFallbackStructure(ServerLevel level, BlockPos origin) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                level.setBlockAndUpdate(origin.offset(x, 0, z), Blocks.OAK_PLANKS.defaultBlockState());
            }
        }

        for (int y = 1; y <= 3; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    boolean wall = Math.abs(x) == 2 || Math.abs(z) == 2;
                    boolean door = x == 0 && z == 2 && (y == 1 || y == 2);
                    BlockPos pos = origin.offset(x, y, z);

                    if (door) {
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    } else if (wall) {
                        level.setBlockAndUpdate(pos, Blocks.OAK_LOG.defaultBlockState());
                    } else {
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }

        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                level.setBlockAndUpdate(origin.offset(x, 4, z), Blocks.OAK_SLAB.defaultBlockState());
            }
        }

        level.setBlockAndUpdate(origin.offset(-1, 1, -1), Blocks.TORCH.defaultBlockState());
        level.setBlockAndUpdate(origin.offset(1, 1, -1), Blocks.TORCH.defaultBlockState());
    }

    private static void clearArea(ServerLevel level, BlockPos start, BlockPos end) {
        for (int x = start.getX(); x <= end.getX(); x++) {
            for (int y = start.getY(); y <= end.getY(); y++) {
                for (int z = start.getZ(); z <= end.getZ(); z++) {
                    level.setBlockAndUpdate(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private static String describeDimension(DimensionDefinition definition) {
        return definition.getStructureDimension() == null ? "<unknown>" : definition.getStructureDimension().location().toString();
    }

    private static final class StructureSpawnSavedData extends SavedData {
        private static final String GENERATED_KEY = "generated";

        private boolean generated;

        private static StructureSpawnSavedData load(CompoundTag tag) {
            StructureSpawnSavedData data = new StructureSpawnSavedData();
            data.generated = tag.getBoolean(GENERATED_KEY);
            return data;
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            tag.putBoolean(GENERATED_KEY, generated);
            return tag;
        }
    }
}
