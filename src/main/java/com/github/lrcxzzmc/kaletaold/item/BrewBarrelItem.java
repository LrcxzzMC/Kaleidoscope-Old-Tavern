package com.github.lrcxzzmc.kaletaold.item;

import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarrelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.Field;

public class BrewBarrelItem extends Item {

    public BrewBarrelItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BarrelBlockEntity barrel)) {
            if (player != null) {
                player.sendSystemMessage(Component.literal("§c请右键点击酒桶的本体（底层中心方块）！"));
            }
            return InteractionResult.FAIL;
        }

        try {
            // 获取酿造等级
            Field field = BarrelBlockEntity.class.getDeclaredField("brewLevel");
            field.setAccessible(true);
            int brewLevel = field.getInt(barrel);

            // 获取满级值
            Field finishedField = BarrelBlockEntity.class.getDeclaredField("BREWING_FINISHED");
            finishedField.setAccessible(true);
            int maxLevel = finishedField.getInt(null);

            // 获取酿造次数（从 NBT 读取）
            var persistentData = be.getPersistentData();
            int brewCount = persistentData.getInt("kaletaold_brew_count");

            // 计算速度加成
            int speedBonus = 0;
            if (brewCount > 0) {
                int maxCount = com.github.lrcxzzmc.kaletaold.Config.COMMON.maxBrewCountForSpeed.get();
                int maxSpeed = com.github.lrcxzzmc.kaletaold.Config.COMMON.maxSpeedPercent.get();
                int effectiveCount = Math.min(brewCount, maxCount);
                if (effectiveCount > 0) {
                    double baseSpeed = 0.05;
                    double speed;
                    if (maxCount == 1) {
                        speed = maxSpeed / 100.0;
                    } else {
                        speed = baseSpeed + (effectiveCount - 1) * ((maxSpeed / 100.0) - baseSpeed) / (maxCount - 1);
                    }
                    speed = Math.min(speed, maxSpeed / 100.0);
                    speed = Math.max(speed, baseSpeed);
                    speedBonus = (int) Math.round(speed * 100);
                }
            }

            // 计算损坏概率
            double baseChance = 0.05;
            double perBrewChance = 0.05;
            double totalChance = baseChance + brewCount * perBrewChance;
            double destroyChance = Math.min(totalChance, 0.55);
            int destroyPercent = (int) Math.round(destroyChance * 100);

            // 构建信息文本
            player.sendSystemMessage(Component.literal("§6=== 酿酒桶信息 ==="));
            player.sendSystemMessage(Component.literal("§e酿造等级: §f" + brewLevel + " / " + maxLevel));
            player.sendSystemMessage(Component.literal("§e完整酿造次数: §f" + brewCount));
            player.sendSystemMessage(Component.literal("§e速度加成: §a+" + speedBonus + "%"));
            player.sendSystemMessage(Component.literal("§e损坏概率: §c" + destroyPercent + "%"));

            // 消耗耐久度（如果有）
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));

        } catch (Exception e) {
            if (player != null) {
                player.sendSystemMessage(Component.literal("§c读取酒桶信息失败！"));
            }
            e.printStackTrace();
        }

        return InteractionResult.SUCCESS;
    }
}