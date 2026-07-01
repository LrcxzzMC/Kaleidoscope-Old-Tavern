package com.github.lrcxzzmc.kaletaold.event;

import com.github.lrcxzzmc.kaletaold.registry.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "kaletaold", value = Dist.CLIENT)
public class ClientEvents {
    private static float prevTime = 0;

    @SubscribeEvent
    public static void onViewport(ViewportEvent.ComputeCameraAngles event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        MobEffectInstance effect = player.getEffect(ModEffects.BEER.get());
        if (effect == null) return;

        // 视角晃动
        float time = (float) (player.tickCount * 0.08);
        float amplitude = 0.04f;
        float yaw = (float) Math.sin(time) * amplitude;
        float pitch = (float) Math.cos(time * 0.7) * amplitude * 0.5f;

        event.setYaw(event.getYaw() + yaw);
        event.setPitch(event.getPitch() + pitch);
    }
}