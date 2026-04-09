package com.y271727uy.ranch_festival.dimension;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import com.y271727uy.ranch_festival.all.ModItem;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import sereneseasons.api.season.Season;

@SuppressWarnings("unused")
public final class DimensionDefinition {
    private static final Set<DimensionDefinition> DEFINITIONS = new LinkedHashSet<>();

    public static final DimensionDefinition TEST = create()
            .Structure((ResourceLocation) null)
            .StructureAxis(0, 64, 0)
            .StructureDimension(ResourceKey.create(
                    Registries.DIMENSION,
                    ResourceLocation.fromNamespaceAndPath(RanchFestivalMod.MODID, "test")
            ))
            .PlayerInputAxis(0, 64, 0)
            .Seasons(Season.SPRING)
            .SubSeason(null)
            .Day(2)
            .SurvivalPlayerDestory(false)
            .AutoReset(true)
            .MobSpawn(false)
            .invitation(ModItem.TEXT_INVITATION)
            .register();

    private final ResourceLocation structure;
    private final BlockPos structureAxis;
    private final ResourceKey<Level> structureDimension;
    private final BlockPos playerInputAxis;
    private final Set<Season> seasons;
    private final Season.SubSeason subSeason;
    private final Integer day;
    private final Boolean survivalPlayerDestroy;
    private final Boolean autoReset;
    private final Boolean mobSpawn;
    private final Supplier<? extends Item> invitation;

    private DimensionDefinition(Builder builder) {
        this.structure = builder.structure;
        this.structureAxis = builder.structureAxis;
        this.structureDimension = builder.structureDimension;
        this.playerInputAxis = builder.playerInputAxis;
        this.seasons = builder.seasons;
        this.subSeason = builder.subSeason;
        this.day = builder.day;
        this.survivalPlayerDestroy = builder.survivalPlayerDestroy;
        this.autoReset = builder.autoReset;
        this.mobSpawn = builder.mobSpawn;
        this.invitation = builder.invitation;
    }

    public static Builder create() {
        return new Builder();
    }

    public static DimensionDefinition register(Builder builder) {
        return register(builder.build());
    }

    public static DimensionDefinition register(DimensionDefinition definition) {
        DEFINITIONS.add(definition);
        return definition;
    }

    public static DimensionDefinition findByDimension(ResourceKey<Level> dimension) {
        for (DimensionDefinition definition : DEFINITIONS) {
            if (Objects.equals(definition.getStructureDimension(), dimension)) {
                return definition;
            }
        }
        return null;
    }

    public static DimensionDefinition findByInvitation(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return null;
        }

        Item item = itemStack.getItem();
        for (DimensionDefinition definition : DEFINITIONS) {
            if (definition.matchesInvitation(item)) {
                return definition;
            }
        }
        return null;
    }

    public ResourceLocation getStructure() {
        return structure;
    }

    public BlockPos getStructureAxis() {
        return structureAxis;
    }

    public ResourceKey<Level> getStructureDimension() {
        return structureDimension;
    }

    public BlockPos getPlayerInputAxis() {
        return playerInputAxis;
    }

    public Set<Season> getSeasons() {
        return seasons;
    }

    public Season.SubSeason getSubSeason() {
        return subSeason;
    }

    public Integer getDay() {
        return day;
    }

    public Boolean getSurvivalPlayerDestroy() {
        return survivalPlayerDestroy;
    }

    public Boolean getAutoReset() {
        return autoReset;
    }

    public Boolean getMobSpawn() {
        return mobSpawn;
    }

    public Item getInvitationItem() {
        return invitation == null ? null : invitation.get();
    }

    public boolean matchesInvitation(Item item) {
        return invitation != null && item != null && getInvitationItem() == item;
    }

    public boolean matchesSeason(Season season) {
        return seasons == null || seasons.isEmpty() || (season != null && seasons.contains(season));
    }

    public boolean matchesSubSeason(Season.SubSeason currentSubSeason) {
        return subSeason == null || Objects.equals(subSeason, currentSubSeason);
    }

    public boolean matchesDay(int currentDay) {
        return day == null || day == currentDay;
    }

    public static final class Builder {
        private ResourceLocation structure;
        private BlockPos structureAxis;
        private ResourceKey<Level> structureDimension;
        private BlockPos playerInputAxis;
        private Set<Season> seasons;
        private Season.SubSeason subSeason;
        private Integer day;
        private Boolean survivalPlayerDestroy;
        private Boolean autoReset;
        private Boolean mobSpawn;
        private Supplier<? extends Item> invitation;

        private Builder() {
        }

        public Builder Structure(String structurePath) {
            this.structure = structurePath == null ? null : ResourceLocation.fromNamespaceAndPath(RanchFestivalMod.MODID, structurePath);
            return this;
        }

        public Builder Structure(ResourceLocation structure) {
            this.structure = structure;
            return this;
        }

        public Builder StructureAxis(int x, int y, int z) {
            this.structureAxis = new BlockPos(x, y, z);
            return this;
        }

        public Builder StructureAxis(BlockPos structureAxis) {
            this.structureAxis = structureAxis;
            return this;
        }

        public Builder StructureDimension(ResourceKey<Level> structureDimension) {
            this.structureDimension = structureDimension;
            return this;
        }

        public Builder PlayerInputAxis(int x, int y, int z) {
            this.playerInputAxis = new BlockPos(x, y, z);
            return this;
        }

        public Builder PlayerInputAxis(BlockPos playerInputAxis) {
            this.playerInputAxis = playerInputAxis;
            return this;
        }

        public Builder Seasons(Season... seasons) {
            if (seasons == null || seasons.length == 0) {
                this.seasons = null;
                return this;
            }

            Set<Season> configuredSeasons = new LinkedHashSet<>();
            Collections.addAll(configuredSeasons, seasons);
            this.seasons = Collections.unmodifiableSet(configuredSeasons);
            return this;
        }

        public Builder SubSeason(Season.SubSeason subSeason) {
            this.subSeason = subSeason;
            return this;
        }

        public Builder Day(Integer day) {
            this.day = day;
            return this;
        }

        public Builder SurvivalPlayerDestory(Boolean survivalPlayerDestroy) {
            this.survivalPlayerDestroy = survivalPlayerDestroy;
            return this;
        }

        public Builder SurvivalPlayerDestroy(Boolean survivalPlayerDestroy) {
            return SurvivalPlayerDestory(survivalPlayerDestroy);
        }

        public Builder AutoReset(Boolean autoReset) {
            this.autoReset = autoReset;
            return this;
        }

        public Builder MobSpawn(Boolean mobSpawn) {
            this.mobSpawn = mobSpawn;
            return this;
        }

        public Builder invitation(Item invitation) {
            return invitation(invitation == null ? null : () -> invitation);
        }

        public Builder invitation(RegistryObject<? extends Item> invitation) {
            return invitation(invitation == null ? null : () -> invitation.get());
        }

        public Builder invitation(Supplier<? extends Item> invitation) {
            this.invitation = invitation;
            return this;
        }

        public DimensionDefinition build() {
            return new DimensionDefinition(this);
        }

        public DimensionDefinition register() {
            return DimensionDefinition.register(build());
        }
    }
}

