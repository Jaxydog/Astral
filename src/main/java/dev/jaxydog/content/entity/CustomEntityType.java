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
import org.jetbrains.annotations.Nullable;

public class CustomEntityType<T extends Entity> extends EntityType<T> implements Registered.All {

	private final String rawId;
	private final @Nullable Runnable register;
	private final @Nullable Runnable registerClient;
	private final @Nullable Runnable registerServer;

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
		FeatureSet requiredFeatures,
		@Nullable Runnable register,
		@Nullable Runnable registerClient,
		@Nullable Runnable registerServer
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
		this.register = register;
		this.registerClient = registerClient;
		this.registerServer = registerServer;
	}

	@Override
	public String getRegistryIdPath() {
		return this.rawId;
	}

	@Override
	public void register() {
		Registry.register(Registries.ENTITY_TYPE, this.getRegistryId(), this);

		if (this.register != null) this.register.run();
	}

	@Override
	public void registerClient() {
		if (this.registerClient != null) this.registerClient.run();
	}

	@Override
	public void registerServer() {
		if (this.registerServer != null) this.registerServer.run();
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
		private @Nullable Runnable register;
		private @Nullable Runnable registerClient;
		private @Nullable Runnable registerServer;

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

		public Builder<T> setRegister(Runnable register) {
			this.register = register;
			return this;
		}

		public Builder<T> setRegisterClient(Runnable register) {
			this.registerClient = register;
			return this;
		}

		public Builder<T> setRegisterServer(Runnable register) {
			this.registerServer = register;
			return this;
		}

		public CustomEntityType<T> build() {
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
				this.requiredFeatures,
				this.register,
				this.registerClient,
				this.registerServer
			);
		}

	}

}
