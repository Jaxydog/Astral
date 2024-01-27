package dev.jaxydog.utility;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.StringIdentifiable;

public enum CowType implements StringIdentifiable {
	BROWN("brown"), PINK("pink");

	public static final TrackedData<String> COW_TYPE = DataTracker.registerData(
		CowEntity.class,
		TrackedDataHandlerRegistry.STRING
	);
	public static final StringIdentifiable.Codec<CowType> CODEC = StringIdentifiable.createCodec(dev.jaxydog.utility.CowType::values);

	private final String name;

	CowType(String name) {
		this.name = name;
	}

	public static CowType fromName(String name) {
		return CODEC.byId(name, BROWN);
	}

	@Override
	public String asString() {
		return this.name;
	}

}
