package com.y271727uy.ranch_festival.dimension;

import com.y271727uy.ranch_festival.season.SeasonAccessChecker;
import com.y271727uy.ranch_festival.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * 维度访问控制器
 * 管理玩家在特定季节条件下进入特殊维度的逻辑
 */
public final class DimensionAccessController {
    private DimensionAccessController() {
    }

    public static boolean isManagedDimension(ResourceKey<Level> targetDimension) {
        return DimensionRegistry.findByDimension(targetDimension) != null;
    }

    public static boolean isManagedDimension(ResourceKey<Level> targetDimension, DimensionDefinition definition) {
        return definition != null && definition.getStructureDimension() != null && definition.getStructureDimension().equals(targetDimension);
    }

    /**
     * 检查实体是否可以进入指定维度
     *
     * @param entity 实体
     * @param targetDimension 目标维度
     * @return true 如果允许进入，false 否则
     */
    public static boolean canEnterDimension(Entity entity, ResourceKey<Level> targetDimension) {
        return canEnterDimension(entity, targetDimension, DimensionRegistry.findByDimension(targetDimension));
    }

    public static boolean canEnterDimension(Entity entity, ResourceKey<Level> targetDimension, DimensionDefinition definition) {
        // 仅对服务器玩家生效
        if (!(entity instanceof ServerPlayer)) {
            return true;
        }

        // 检查是否为受季节控制的节日维度
        if (isManagedDimension(targetDimension, definition)) {
            // 检查是否启用了季节锁
            if (!Config.enableTheDimensionSeasonLock) {
                return true; // 如果禁用了季节锁定，则始终允许访问
            }
            
            // 使用特定的 test 维度访问检查
            return SeasonAccessChecker.isDimensionAccessAllowed(((ServerPlayer) entity).serverLevel(), definition);
        }

        return true; // 不是我们管理的维度，允许进入
    }
}