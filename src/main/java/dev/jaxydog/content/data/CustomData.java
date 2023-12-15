package dev.jaxydog.content.data;

import java.util.List;
import dev.jaxydog.utility.register.ContentContainer;
import dev.jaxydog.utility.register.Skip;
import io.github.apace100.calio.SerializationHelper;
import io.github.apace100.calio.data.SerializableDataType;

/** Contains shared mod data types */
public final class CustomData extends ContentContainer {

	/** The moon phase data type */
	@Skip
	public static final SerializableDataType<MoonPhase> MOON_PHASE = SerializableDataType.enumValue(
		MoonPhase.class,
		SerializationHelper.buildEnumMap(MoonPhase.class, MoonPhase::getName));

	/** A list of moon phase data types */
	@Skip
	public static final SerializableDataType<List<MoonPhase>> MOON_PHASES = SerializableDataType
		.list(CustomData.MOON_PHASE);

	/** The scale operation data type */
	public static final SerializableDataType<ScaleOperation> SCALE_OPERATION = SerializableDataType.enumValue(
		ScaleOperation.class,
		SerializationHelper.buildEnumMap(ScaleOperation.class, ScaleOperation::getName));
}
