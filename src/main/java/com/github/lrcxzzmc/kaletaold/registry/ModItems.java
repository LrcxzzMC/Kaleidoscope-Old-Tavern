package com.github.lrcxzzmc.kaletaold.registry;

import com.github.lrcxzzmc.kaletaold.KaletaOldMod;
import com.github.lrcxzzmc.kaletaold.item.BrewBarrelItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, KaletaOldMod.MOD_ID);

    public static final RegistryObject<Item> BREW_BARREL_VIEWER =
            ITEMS.register("brew_barrel_viewer",
                    () -> new BrewBarrelItem(new Item.Properties()
                            .stacksTo(1)
                            .durability(256)
                    ));

    // 如果需要自定义创造模式标签页，可以取消注释
    // public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
    //         DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KaletaOldMod.MOD_ID);
    //
    // public static final RegistryObject<CreativeModeTab> KALETAOLD_TAB =
    //         CREATIVE_MODE_TABS.register("kaletaold_tab",
    //                 () -> CreativeModeTab.builder()
    //                         .title(Component.translatable("itemGroup.kaletaold"))
    //                         .icon(() -> BREW_BARREL_VIEWER.get().getDefaultInstance())
    //                         .displayItems((parameters, output) -> {
    //                             output.accept(BREW_BARREL_VIEWER.get());
    //                         })
    //                         .build());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        // CREATIVE_MODE_TABS.register(eventBus);
    }
}