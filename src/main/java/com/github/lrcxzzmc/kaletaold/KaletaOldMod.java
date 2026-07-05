package com.github.lrcxzzmc.kaletaold;

import com.github.lrcxzzmc.kaletaold.command.BrewDebugCommand;
import com.github.lrcxzzmc.kaletaold.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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

        // 注册物品
        ModItems.register(FMLJavaModLoadingContext.get().getModEventBus());

        // 监听创造模式标签页构建事件
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::buildCreativeModeTabContents);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        LOGGER.info("森罗物语：老牌酒馆 加载成功！");
    }

    private void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(BrewDebugCommand.register());
    }

    /**
     * 将酒桶状态查看器添加到原版"工具与实用物品"标签页
     */
    private void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        // 检查是否为"工具与实用物品"标签页
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.BREW_BARREL_VIEWER.get());
        }
    }
}