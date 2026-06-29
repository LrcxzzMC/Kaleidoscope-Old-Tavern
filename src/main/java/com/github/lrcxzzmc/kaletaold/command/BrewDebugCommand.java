package com.github.lrcxzzmc.kaletaold.command;

import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarrelBlockEntity;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Field;
import java.util.Random;

public class BrewDebugCommand {

    private static final Random RANDOM = new Random();

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("brewdebug")
                .requires(source -> source.hasPermission(2))
                // ===== finish =====
                .then(Commands.literal("finish")
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();
                            var hitResult = player.pick(20.0D, 0.0F, false);
                            var pos = BlockPos.containing(hitResult.getLocation());
                            var level = player.level();

                            BlockEntity be = level.getBlockEntity(pos);
                            if (!(be instanceof BarrelBlockEntity barrel)) {
                                context.getSource().sendFailure(Component.literal("§c请看向酿酒桶的本体（底层中心方块）！"));
                                return 0;
                            }

                            try {
                                Field finishedField = BarrelBlockEntity.class.getDeclaredField("BREWING_FINISHED");
                                finishedField.setAccessible(true);
                                int maxLevel = finishedField.getInt(null);

                                Field field = BarrelBlockEntity.class.getDeclaredField("brewLevel");
                                field.setAccessible(true);
                                field.setInt(barrel, maxLevel);

                                barrel.refresh();
                                BlockState state = level.getBlockState(pos);
                                level.sendBlockUpdated(pos, state, state, 3);

                                context.getSource().sendSuccess(
                                        () -> Component.literal("§a✅ 酿酒桶已立即完成酿造！"),
                                        true
                                );
                            } catch (Exception e) {
                                context.getSource().sendFailure(
                                        Component.literal("§c设置失败: " + e.getMessage())
                                );
                            }
                            return 1;
                        })
                )
                // ===== setlevel =====
                .then(Commands.literal("setlevel")
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 10))
                                .executes(context -> {
                                    var player = context.getSource().getPlayerOrException();
                                    var hitResult = player.pick(20.0D, 0.0F, false);
                                    var pos = BlockPos.containing(hitResult.getLocation());
                                    var level = player.level();
                                    int targetLevel = IntegerArgumentType.getInteger(context, "level");

                                    BlockEntity be = level.getBlockEntity(pos);
                                    if (!(be instanceof BarrelBlockEntity barrel)) {
                                        context.getSource().sendFailure(Component.literal("§c请看向酿酒桶的本体（底层中心方块）！"));
                                        return 0;
                                    }

                                    try {
                                        Field field = BarrelBlockEntity.class.getDeclaredField("brewLevel");
                                        field.setAccessible(true);
                                        field.setInt(barrel, targetLevel);
                                        barrel.refresh();
                                        BlockState state = level.getBlockState(pos);
                                        level.sendBlockUpdated(pos, state, state, 3);

                                        context.getSource().sendSuccess(
                                                () -> Component.literal("§a✅ 酿酒桶等级已设为 " + targetLevel),
                                                true
                                        );
                                    } catch (Exception e) {
                                        context.getSource().sendFailure(
                                                Component.literal("§c设置失败: " + e.getMessage())
                                        );
                                    }
                                    return 1;
                                })
                        )
                )
                // ===== addcount =====
                .then(Commands.literal("addcount")
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();
                            var hitResult = player.pick(20.0D, 0.0F, false);
                            var pos = BlockPos.containing(hitResult.getLocation());
                            var level = player.level();

                            BlockEntity be = level.getBlockEntity(pos);
                            if (!(be instanceof BarrelBlockEntity barrel)) {
                                context.getSource().sendFailure(Component.literal("§c请看向酿酒桶的本体（底层中心方块）！"));
                                return 0;
                            }

                            var persistentData = be.getPersistentData();
                            int current = persistentData.getInt("kaletaold_brew_count");
                            persistentData.putInt("kaletaold_brew_count", current + 1);
                            be.setChanged();

                            BlockState state = level.getBlockState(pos);
                            level.sendBlockUpdated(pos, state, state, 3);

                            context.getSource().sendSuccess(
                                    () -> Component.literal("§a✅ 次数已增加！当前次数: " + (current + 1)),
                                    true
                            );
                            return 1;
                        })
                )
                // ===== setcount =====
                .then(Commands.literal("setcount")
                        .then(Commands.argument("count", IntegerArgumentType.integer(0, 100))
                                .executes(context -> {
                                    var player = context.getSource().getPlayerOrException();
                                    var hitResult = player.pick(20.0D, 0.0F, false);
                                    var pos = BlockPos.containing(hitResult.getLocation());
                                    var level = player.level();
                                    int targetCount = IntegerArgumentType.getInteger(context, "count");

                                    BlockEntity be = level.getBlockEntity(pos);
                                    if (!(be instanceof BarrelBlockEntity barrel)) {
                                        context.getSource().sendFailure(Component.literal("§c请看向酿酒桶的本体（底层中心方块）！"));
                                        return 0;
                                    }

                                    var persistentData = be.getPersistentData();
                                    persistentData.putInt("kaletaold_brew_count", targetCount);
                                    be.setChanged();

                                    BlockState state = level.getBlockState(pos);
                                    level.sendBlockUpdated(pos, state, state, 3);

                                    context.getSource().sendSuccess(
                                            () -> Component.literal("§a✅ 次数已设为 " + targetCount),
                                            true
                                    );
                                    return 1;
                                })
                        )
                )
                // ===== damage =====
                .then(Commands.literal("damage")
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();
                            var hitResult = player.pick(20.0D, 0.0F, false);
                            var pos = BlockPos.containing(hitResult.getLocation());
                            var level = player.level();

                            BlockEntity be = level.getBlockEntity(pos);
                            if (!(be instanceof BarrelBlockEntity)) {
                                context.getSource().sendFailure(Component.literal("§c请看向酿酒桶的本体（底层中心方块）！"));
                                return 0;
                            }

                            // 移除 3x3x3 范围内的所有方块
                            for (int dx = -1; dx <= 1; dx++) {
                                for (int dy = 0; dy <= 2; dy++) {
                                    for (int dz = -1; dz <= 1; dz++) {
                                        BlockPos targetPos = pos.offset(dx, dy, dz);
                                        BlockState state = level.getBlockState(targetPos);
                                        if (!state.isAir()) {
                                            level.removeBlock(targetPos, false);
                                        }
                                    }
                                }
                            }

                            level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                    2.0f, Level.ExplosionInteraction.NONE);

                            level.playSound(null, pos, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR,
                                    SoundSource.BLOCKS, 1.0f, 1.0f);

                            if (level instanceof ServerLevel serverLevel) {
                                for (int i = 0; i < 80; i++) {
                                    double xOff = (RANDOM.nextDouble() - 0.5) * 5;
                                    double yOff = RANDOM.nextDouble() * 4;
                                    double zOff = (RANDOM.nextDouble() - 0.5) * 5;
                                    serverLevel.sendParticles(
                                            ParticleTypes.EXPLOSION_EMITTER,
                                            pos.getX() + 0.5 + xOff,
                                            pos.getY() + 0.5 + yOff,
                                            pos.getZ() + 0.5 + zOff,
                                            1, 0, 0, 0, 0
                                    );
                                }
                                for (int i = 0; i < 50; i++) {
                                    double xOff = (RANDOM.nextDouble() - 0.5) * 4;
                                    double yOff = RANDOM.nextDouble() * 3;
                                    double zOff = (RANDOM.nextDouble() - 0.5) * 4;
                                    serverLevel.sendParticles(
                                            ParticleTypes.FLAME,
                                            pos.getX() + 0.5 + xOff,
                                            pos.getY() + 0.5 + yOff,
                                            pos.getZ() + 0.5 + zOff,
                                            1, 0, 0, 0, 0
                                    );
                                }
                            }

                            context.getSource().sendSuccess(
                                    () -> Component.literal("§c💥 酒桶已完全损坏！"),
                                    true
                            );
                            return 1;
                        })
                )
                // ===== info =====
                .then(Commands.literal("info")
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();
                            var hitResult = player.pick(20.0D, 0.0F, false);
                            var pos = BlockPos.containing(hitResult.getLocation());
                            var level = player.level();

                            BlockEntity be = level.getBlockEntity(pos);
                            if (!(be instanceof BarrelBlockEntity barrel)) {
                                context.getSource().sendFailure(Component.literal("§c请看向酿酒桶的本体（底层中心方块）！"));
                                return 0;
                            }

                            try {
                                Field field = BarrelBlockEntity.class.getDeclaredField("brewLevel");
                                field.setAccessible(true);
                                int brewLevel = field.getInt(barrel);

                                Field finishedField = BarrelBlockEntity.class.getDeclaredField("BREWING_FINISHED");
                                finishedField.setAccessible(true);
                                int maxLevel = finishedField.getInt(null);

                                var persistentData = be.getPersistentData();
                                int brewCount = persistentData.getInt("kaletaold_brew_count");

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

                                double baseChance = 0.05;
                                double perBrewChance = 0.05;
                                double totalChance = baseChance + brewCount * perBrewChance;
                                double destroyChance = Math.min(totalChance, 0.55);
                                int destroyPercent = (int) Math.round(destroyChance * 100);

                                int finalBrewLevel = brewLevel;
                                int finalMaxLevel = maxLevel;
                                int finalBrewCount = brewCount;
                                int finalSpeedBonus = speedBonus;
                                int finalDestroyPercent = destroyPercent;

                                context.getSource().sendSuccess(
                                        () -> Component.literal(
                                                "§6=== 酿酒桶信息 ===\n" +
                                                        "§e当前酿造等级: §f" + finalBrewLevel + " / " + finalMaxLevel + "\n" +
                                                        "§e已完成的完整酿造次数: §f" + finalBrewCount + "\n" +
                                                        "§e当前速度加成: §a+" + finalSpeedBonus + "%\n" +
                                                        "§e下次损坏概率: §c" + finalDestroyPercent + "%"
                                        ),
                                        true
                                );
                            } catch (Exception e) {
                                context.getSource().sendFailure(
                                        Component.literal("§c获取信息失败: " + e.getMessage())
                                );
                            }
                            return 1;
                        })
                );
    }
}