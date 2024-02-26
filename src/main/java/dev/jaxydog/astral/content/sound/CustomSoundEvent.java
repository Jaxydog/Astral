package dev.jaxydog.astral.content.sound;

import dev.jaxydog.astral.Astral;
import dev.jaxydog.astral.register.Registered;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class CustomSoundEvent extends SoundEvent implements Registered.Common {

    private final String RAW_ID;

    private CustomSoundEvent(String rawId, float distanceTraveled, boolean useStaticDistance) {
        super(Astral.getId(rawId), distanceTraveled, useStaticDistance);

        this.RAW_ID = rawId;
    }

    @SuppressWarnings("unused")
    public CustomSoundEvent(String rawId, float distanceTraveled) {
        this(rawId, distanceTraveled, true);
    }

    public CustomSoundEvent(String rawId) {
        this(rawId, 16F, false);
    }

    @Override
    public String getRegistryIdPath() {
        return this.RAW_ID;
    }

    @Override
    public void register() {
        Registry.register(Registries.SOUND_EVENT, this.getId(), this);
    }

}
