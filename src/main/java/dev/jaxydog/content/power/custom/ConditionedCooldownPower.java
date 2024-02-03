package dev.jaxydog.content.power.custom;

import dev.jaxydog.content.power.CustomPower;
import dev.jaxydog.content.power.CustomPowerFactory;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.HudRendered;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ConditionedCooldownPower extends CustomPower implements HudRendered {

    private final int duration;
    private final HudRender hudRender;
    private final @Nullable Predicate<Entity> tickCondition;

    protected int progress;

    public ConditionedCooldownPower(
        PowerType<?> type,
        LivingEntity entity,
        int duration,
        HudRender hudRender,
        @Nullable Predicate<Entity> tickCondition
    ) {
        super(type, entity);

        this.progress = this.duration = duration;
        this.hudRender = hudRender;
        this.tickCondition = tickCondition;

        this.setTicking(true);
    }

    public static CustomPowerFactory<ConditionedCooldownPower> createConditionedCooldownFactory() {
        return new CustomPowerFactory<ConditionedCooldownPower>("conditioned_cooldown",
            new SerializableData().add("cooldown", SerializableDataTypes.INT)
                .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                .add("tick_condition", ApoliDataTypes.ENTITY_CONDITION, null),
            data -> (type, player) -> new ConditionedCooldownPower(type,
                player,
                data.getInt("cooldown"),
                data.get("hud_render"),
                data.get("tick_condition")
            )
        ).allowCondition();
    }

    public boolean canUse() {
        return this.progress >= this.duration && this.isActive();
    }

    public void use() {
        this.progress = 0;

        PowerHolderComponent.syncPower(this.entity, this.type);
    }

    public float getProgress() {
        return this.getFill();
    }

    public int getRemainingTicks() {
        return this.duration - this.progress;
    }

    public void modify(int changeInTicks) {
        this.progress -= changeInTicks;
        this.progress = MathHelper.clamp(this.progress, 0, this.duration);
    }

    public void setCooldown(int cooldownInTicks) {
        this.progress = this.duration - cooldownInTicks;
    }

    @Override
    public void tick() {
        if (this.canUse()) return;

        if (this.tickCondition == null || this.tickCondition.test(this.entity)) {
            this.progress += 1;

            PowerHolderComponent.syncPower(this.entity, this.type);
        }
    }

    @Override
    public NbtElement toTag() {
        return NbtInt.of(this.progress);
    }

    @Override
    public void fromTag(NbtElement tag) {
        this.progress = ((NbtInt) tag).intValue();
    }

    @Override
    public HudRender getRenderSettings() {
        return this.hudRender;
    }

    @Override
    public float getFill() {
        return MathHelper.clamp((float) this.progress / (float) this.duration, 0F, 1F);
    }

    @Override
    public boolean shouldRender() {
        return this.progress < this.duration;
    }

}
