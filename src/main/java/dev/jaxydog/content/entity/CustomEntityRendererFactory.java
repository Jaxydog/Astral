package dev.jaxydog.content.entity;

import dev.jaxydog.register.Registered;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry.TexturedModelDataProvider;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.Map;

@Environment(EnvType.CLIENT)
public abstract class CustomEntityRendererFactory<T extends Entity> implements Registered.Client {

	private final String rawId;
	private final EntityType<? extends T> entityType;
	private final Map<EntityModelLayer, TexturedModelDataProvider> layers;

	public CustomEntityRendererFactory(
		String rawId, EntityType<? extends T> entityType, Map<EntityModelLayer, TexturedModelDataProvider> layers
	) {
		this.rawId = rawId;
		this.entityType = entityType;
		this.layers = layers;
	}

	public abstract EntityRenderer<T> create(Context context);

	@Override
	public String getIdPath() {
		return this.rawId;
	}

	@Override
	public void registerClient() {
		EntityRendererRegistry.register(this.entityType, this::create);

		this.layers.forEach(EntityModelLayerRegistry::registerModelLayer);
	}

}
