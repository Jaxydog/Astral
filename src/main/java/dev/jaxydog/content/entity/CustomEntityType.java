package dev.jaxydog.content.entity;

import com.google.common.collect.ImmutableSet;
import dev.jaxydog.register.Registered;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class CustomEntityType<T extends Entity> extends EntityType<T> implements Registered.Common {

	private final String rawId;

	// this is awful.
	public CustomEntityType(
		String rawId,
		EntityFactory<T> factory,
		SpawnGroup spawnGroup,
		boolean saveable,
		boolean summonable,
		boolean fireImmune,
		boolean spawnableFarFromPlayer,
		ImmutableSet<Block> canSpawnInside,
		EntityDimensions dimensions,
		int maxTrackDistance,
		int trackTickInterval,
		FeatureSet requiredFeatures
	) {
		super(
			factory,
			spawnGroup,
			saveable,
			summonable,
			fireImmune,
			spawnableFarFromPlayer,
			canSpawnInside,
			dimensions,
			maxTrackDistance,
			trackTickInterval,
			requiredFeatures
		);

		this.rawId = rawId;
	}

	@Override
	public String getRegistryIdPath() {
		return this.rawId;
	}

	@Override
	public void register() {
		Registry.register(Registries.ENTITY_TYPE, this.getRegistryId(), this);
	}

	public static class Builder<T extends Entity> {

		private final String rawId;
		private final SpawnGroup spawnGroup;
		private final EntityFactory<T> entityFactory;
		private boolean saveable;
		private boolean summonable;
		private boolean fireImmune;
		private boolean spawnableFarFromPlayer;
		private ImmutableSet<Block> canSpawnInside;
		private EntityDimensions dimensions;
		private int maxTrackDistance;
		private int trackTickInterval;
		private FeatureSet requiredFeatures;

		protected Builder(String rawId, SpawnGroup spawnGroup, EntityFactory<T> entityFactory) {
			assert spawnGroup != null;
			assert entityFactory != null;

			this.rawId = rawId;
			this.spawnGroup = spawnGroup;
			this.entityFactory = entityFactory;
		}

		public static <T extends Entity> Builder<T> create(
			String rawId, SpawnGroup spawnGroup, EntityFactory<T> entityFactory
		) {
			return new Builder<>(rawId, spawnGroup, entityFactory);
		}

		public static <T extends Entity> Builder<T> create(String rawId, SpawnGroup spawnGroup) {
			return create(rawId, spawnGroup, (type, world) -> null);
		}

		public static <T extends Entity> Builder<T> create(String rawId) {
			return create(rawId, SpawnGroup.MISC);
		}

		public Builder<T> setSaveable(boolean saveable) {
			this.saveable = saveable;
			return this;
		}

		public Builder<T> setSummonable(boolean summonable) {
			this.summonable = summonable;
			return this;
		}

		public Builder<T> setFireImmune(boolean immune) {
			this.fireImmune = immune;
			return this;
		}

		public Builder<T> setSpawnableFarFromPlayer(boolean player) {
			this.spawnableFarFromPlayer = player;
			return this;
		}

		public Builder<T> setCanSpawnInside(ImmutableSet<Block> inside) {
			this.canSpawnInside = inside;
			return this;
		}

		public Builder<T> setDimensions(EntityDimensions dimensions) {
			this.dimensions = dimensions;
			return this;
		}

		public Builder<T> setMaxTrackDistance(int distance) {
			this.maxTrackDistance = distance;
			return this;
		}

		public Builder<T> setTrackTickInterval(int interval) {
			this.trackTickInterval = interval;
			return this;
		}

		public Builder<T> setRequiredFeatures(FeatureSet features) {
			this.requiredFeatures = features;
			return this;
		}

		public CustomEntityType<T> createCustomEntityType() {
			return new CustomEntityType<>(
				this.rawId,
				this.entityFactory,
				this.spawnGroup,
				this.saveable,
				this.summonable,
				this.fireImmune,
				this.spawnableFarFromPlayer,
				this.canSpawnInside,
				this.dimensions,
				this.maxTrackDistance,
				this.trackTickInterval,
				this.requiredFeatures
			);
		}

	}

}
