package com.github.lrcxzzmc.kaletaold;

import com.github.lrcxzzmc.kaletaold.registry.ModEffects;
import com.github.lrcxzzmc.kaletaold.registry.ModItems;
import com.github.lrcxzzmc.kaletaold.command.BrewDebugCommand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(KaletaOldMod.MOD_ID)
public class KaletaOldMod {
    public static final String MOD_ID = "kaletaold";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public KaletaOldMod() {
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModEffects.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        LOGGER.info("森罗物语：老牌酒馆 加载成功！");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 配方1：水瓶 + 啤酒花 → 啤酒花风味化合物
            BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
                    Ingredient.of(Items.POTION),
                    Ingredient.of(ModItems.HOPS.get()),
                    new ItemStack(ModItems.HOPS_COMPOUND.get())
            ));

            // 配方2：面包 + 啤酒花风味化合物 → 啤酒
            BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
                    Ingredient.of(Items.BREAD),
                    Ingredient.of(ModItems.HOPS_COMPOUND.get()),
                    new ItemStack(ModItems.BEER.get())
            ));
        });
    }

    private void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(BrewDebugCommand.register());
    }
}