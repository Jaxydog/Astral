package dev.jaxydog.content.power.custom;

import dev.jaxydog.content.power.CustomPowerFactory;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
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

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TickingCooldownPower extends CooldownPower {

    private final @Nullable Predicate<Entity> tickCondition;
    private final @Nullable Consumer<Entity> minAction;
    private final @Nullable Consumer<Entity> setAction;
    private final @Nullable Consumer<Entity> maxAction;

    protected int progress;

    public TickingCooldownPower(
        PowerType<?> type,
        LivingEntity entity,
        int duration,
        HudRender hudRender,
        @Nullable Predicate<Entity> tickCondition,
        @Nullable Consumer<Entity> minAction,
        @Nullable Consumer<Entity> setAction,
        @Nullable Consumer<Entity> maxAction
    ) {
        super(type, entity, duration, hudRender);

        this.tickCondition = tickCondition;
        this.minAction = minAction;
        this.setAction = setAction;
        this.maxAction = maxAction;
        this.progress = duration;

        this.setTicking(true);
    }

    public static CustomPowerFactory<TickingCooldownPower> getCooldownFactory() {
        return new CustomPowerFactory<>(
            "ticking_cooldown",
            new SerializableData().add("cooldown", SerializableDataTypes.INT)
                .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                .add("tick_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                .add("min_action", ApoliDataTypes.ENTITY_ACTION, null)
                .add("set_action", ApoliDataTypes.ENTITY_ACTION, null)
                .add("max_action", ApoliDataTypes.ENTITY_ACTION, null),
            data -> (type, entity) -> new TickingCooldownPower(
                type,
                entity,
                data.getInt("cooldown"),
                data.get("hud_render"),
                data.get("tick_condition"),
                data.get("min_action"),
                data.get("set_action"),
                data.get("max_action")
            )
        );
    }

    @Override
    public boolean canUse() {
        return this.progress >= this.cooldownDuration && this.isActive();
    }

    @Override
    public void use() {
        this.setCooldown(this.cooldownDuration);

        PowerHolderComponent.syncPower(this.entity, this.type);
    }

    @Override
    public float getProgress() {
        return MathHelper.clamp((float) this.progress / (float) this.cooldownDuration, 0F, 1F);
    }

    @Override
    public int getRemainingTicks() {
        return MathHelper.clamp(this.cooldownDuration - this.progress, 0, this.cooldownDuration);
    }

    @Override
    public void modify(int changeInTicks) {
        this.progress = MathHelper.clamp(this.progress - changeInTicks, 0, this.cooldownDuration);

        if (this.setAction != null) this.setAction.accept(this.entity);
    }

    @Override
    public void setCooldown(int cooldownInTicks) {
        this.progress = MathHelper.clamp(this.cooldownDuration - cooldownInTicks, 0, this.cooldownDuration);

        if (this.setAction != null) this.setAction.accept(this.entity);
    }

    @Override
    public void tick() {
        if (this.progress >= this.cooldownDuration) return;

        if (this.progress <= 0 && this.minAction != null) {
            this.minAction.accept(this.entity);
        }

        if (this.tickCondition == null || this.tickCondition.test(this.entity)) {
            this.progress += 1;

            PowerHolderComponent.syncPower(this.entity, this.type);
        }

        if (this.progress >= this.cooldownDuration && this.maxAction != null) {
            this.maxAction.accept(this.entity);
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
    public boolean shouldRender() {
        return this.progress < this.cooldownDuration;
    }

}
