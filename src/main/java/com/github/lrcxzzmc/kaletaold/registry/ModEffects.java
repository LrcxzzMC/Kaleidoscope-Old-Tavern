package com.github.lrcxzzmc.kaletaold.registry;

import com.github.lrcxzzmc.kaletaold.KaletaOldMod;
import com.github.lrcxzzmc.kaletaold.effect.BeerEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, KaletaOldMod.MOD_ID);

    public static final RegistryObject<MobEffect> BEER =
            EFFECTS.register("beer",
                    () -> new BeerEffect(MobEffectCategory.BENEFICIAL, 0xFFD700));

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}