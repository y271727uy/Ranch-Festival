package com.y271727uy.ranch_festival.event.Structure;

import com.y271727uy.ranch_festival.dimension.DimensionDefinition;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class StructureTemplatePlacer {
    private StructureTemplatePlacer() {
    }

    public static boolean tryPlace(ServerLevel level, DimensionDefinition definition) {
        if (definition.getStructure() == null || definition.getStructureAxis() == null) {
            return false;
        }

        try {
            StructureTemplateManager manager = level.getStructureManager();
            StructureTemplate template = manager.getOrCreate(definition.getStructure());
            StructurePlaceSettings settings = new StructurePlaceSettings()
                    .setMirror(Mirror.NONE)
                    .setRotation(Rotation.NONE)
                    .setIgnoreEntities(false);

            return template.placeInWorld(
                    level,
                    definition.getStructureAxis(),
                    definition.getStructureAxis(),
                    settings,
                    RandomSource.create(),
                    2
            );
        } catch (Exception e) {
            return false;
        }
    }
}


