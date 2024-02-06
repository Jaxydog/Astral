package dev.jaxydog.content.power.custom;

import dev.jaxydog.content.power.CustomPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.modifier.Modifier;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleType;

import java.util.List;

public class ModifyScalePower extends CustomPower {

    private final int delay;
    private final List<ScaleType> types;
    private final @Nullable Modifier modifier;
    private final @Nullable List<Modifier> modifiers;

    private boolean appliedScales = false;

    public ModifyScalePower(
        PowerType<?> type,
        LivingEntity entity,
        int delay,
        List<ScaleType> types,
        @Nullable Modifier modifier,
        @Nullable List<Modifier> modifiers
    ) {
        super(type, entity);

        this.delay = delay;
        this.types = types;
        this.modifier = modifier;
        this.modifiers = modifiers;

        this.setTicking(false);
    }

    private ScaleData getScaleData(ScaleType type) {
        return ScaleData.Builder.create().entity(this.entity).type(type).build();
    }

    private float getModifiedScale(float initial) {
        // Cast to double beforehand for better precision
        double scale = initial;

        if (this.modifier != null) {
            scale = this.modifier.apply(this.entity, scale);
        }

        if (this.modifiers != null) {
            for (final Modifier modifier : this.modifiers) {
                scale = modifier.apply(this.entity, scale);
            }
        }

        return (float) scale;
    }

    @Override
    public void tick() {
        if (this.appliedScales) return;

        for (final ScaleType type : this.types) {
            final ScaleData data = this.getScaleData(type);
            final float scale = this.getModifiedScale(data.getInitialScale());

            data.setScaleTickDelay(this.delay);
            data.setTargetScale(scale);
        }

        this.appliedScales = true;
    }

}
