package dev.jaxydog.astral.content.data;

import dev.jaxydog.astral.content.data.custom.MoonPhase;
import dev.jaxydog.astral.utility.register.AutoRegister;
import dev.jaxydog.astral.utility.register.SkipRegistration;
import io.github.apace100.calio.SerializationHelper;
import io.github.apace100.calio.data.SerializableDataType;
import java.util.List;

/** Contains commonly shared data types */
@AutoRegister
public final class CustomData {

	@SkipRegistration
	public static final SerializableDataType<MoonPhase> MOON_PHASE = SerializableDataType.enumValue(
		MoonPhase.class,
		SerializationHelper.buildEnumMap(MoonPhase.class, MoonPhase::getName)
	);

	@SkipRegistration
	public static final SerializableDataType<List<MoonPhase>> MOON_PHASES = SerializableDataType.list(
		CustomData.MOON_PHASE
	);

	private CustomData() {}
}
