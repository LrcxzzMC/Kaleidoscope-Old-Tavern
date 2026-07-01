package com.github.lrcxzzmc.kaletaold.registry;

import com.github.lrcxzzmc.kaletaold.KaletaOldMod;
import com.github.lrcxzzmc.kaletaold.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, KaletaOldMod.MOD_ID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KaletaOldMod.MOD_ID);

    // 酒桶状态查看器
    public static final RegistryObject<Item> BREW_BARREL_VIEWER =
            ITEMS.register("brew_barrel_viewer",
                    () -> new BrewBarrelItem(new Item.Properties()
                            .stacksTo(1)
                            .durability(256)));

    // 啤酒花（已有）
    public static final RegistryObject<Item> HOPS =
            ITEMS.register("hops",
                    () -> new HopsItem(new Item.Properties()));

    // 啤酒花风味化合物
    public static final RegistryObject<Item> HOPS_COMPOUND =
            ITEMS.register("hops_compound",
                    () -> new HopsCompoundItem(new Item.Properties()
                            .stacksTo(16)
                            .food(new FoodProperties.Builder()
                                    .alwaysEat()
                                    .effect(() -> new net.minecraft.world.effect.MobEffectInstance(
                                            net.minecraft.world.effect.MobEffects.CONFUSION, 60, 0), 1.0F)
                                    .build())));

    // 啤酒
    public static final RegistryObject<Item> BEER =
            ITEMS.register("beer",
                    () -> new BeerItem(new Item.Properties()
                            .stacksTo(16)
                            .food(new FoodProperties.Builder()
                                    .nutrition(3)
                                    .saturationMod(0.1f)
                                    .alwaysEat()
                                    .effect(() -> new net.minecraft.world.effect.MobEffectInstance(
                                            ModEffects.BEER.get(), 180 * 20, 0), 1.0F)
                                    .build())));

    // 创造模式标签页
    public static final RegistryObject<CreativeModeTab> KALETAOLD_TAB =
            CREATIVE_MODE_TABS.register("kaletaold_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.kaletaold"))
                            .icon(() -> BREW_BARREL_VIEWER.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                output.accept(BREW_BARREL_VIEWER.get());
                                output.accept(HOPS.get());
                                output.accept(HOPS_COMPOUND.get());
                                output.accept(BEER.get());
                            })
                            .build());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        CREATIVE_MODE_TABS.register(eventBus);
    }
}