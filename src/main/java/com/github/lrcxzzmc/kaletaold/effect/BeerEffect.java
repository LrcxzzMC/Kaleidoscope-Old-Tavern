package com.github.lrcxzzmc.kaletaold.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BeerEffect extends MobEffect {
    private static final float FLOAT_AMPLITUDE = 1.5F;

    public BeerEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        if (living instanceof Player player) {
            // 回血加速
            if (living.getHealth() < living.getMaxHealth()) {
                living.heal(0.01F);
            }

            // 飘浮效果（视角晃动在渲染层处理）
            double time = living.tickCount * 0.05;
            double floatOffset = Math.sin(time) * FLOAT_AMPLITUDE;

            if (!player.onGround() || floatOffset > 0) {
                if (floatOffset > 0 && player.getDeltaMovement().y < 0.1) {
                    player.setDeltaMovement(player.getDeltaMovement().x, 0.05, player.getDeltaMovement().z);
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}