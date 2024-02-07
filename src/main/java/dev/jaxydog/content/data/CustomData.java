package dev.jaxydog.content.data;

import dev.jaxydog.register.ContentRegistrar;
import dev.jaxydog.register.IgnoreRegistration;
import io.github.apace100.calio.SerializationHelper;
import io.github.apace100.calio.data.SerializableDataType;

import java.util.List;

/** Contains shared mod data types */
public final class CustomData extends ContentRegistrar {

    /** The moon phase data type */
    @IgnoreRegistration
    public static final SerializableDataType<MoonPhase> MOON_PHASE = SerializableDataType.enumValue(MoonPhase.class,
        SerializationHelper.buildEnumMap(MoonPhase.class, MoonPhase::getName)
    );

    /** A list of moon phase data types */
    @IgnoreRegistration
    public static final SerializableDataType<List<MoonPhase>> MOON_PHASES = SerializableDataType.list(MOON_PHASE);

    /** The scale operation data type */
    @IgnoreRegistration
    @Deprecated(since = "1.7.0", forRemoval = true)
    public static final SerializableDataType<ScaleOperation> SCALE_OPERATION = SerializableDataType.enumValue(ScaleOperation.class,
        SerializationHelper.buildEnumMap(ScaleOperation.class, ScaleOperation::getName)
    );

}
