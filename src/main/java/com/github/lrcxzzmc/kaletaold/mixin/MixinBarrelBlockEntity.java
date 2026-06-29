package com.github.lrcxzzmc.kaletaold.mixin;

import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarrelBlockEntity;
import com.github.ysbbbbbb.kaleidoscopetavern.crafting.recipe.BarrelRecipe;
import com.github.ysbbbbbb.kaleidoscopetavern.crafting.serializer.BarrelRecipeSerializer;
import com.github.lrcxzzmc.kaletaold.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = BarrelBlockEntity.class, remap = false)
public abstract class MixinBarrelBlockEntity {

    @Shadow
    private int brewLevel;
    @Shadow
    private ResourceLocation recipeId;
    @Shadow
    private int brewTime;
    @Shadow
    private ItemStackHandler output;
    @Shadow
    public abstract boolean isMaxBrewLevel();
    @Shadow
    public abstract int getBrewLevel();

    @Unique
    private static final int BREWING_FINISHED = 7;
    @Unique
    private static final Random RANDOM = new Random();
    @Unique
    private static final String BREW_COUNT_KEY = "kaletaold_brew_count";
    @Unique
    private int kaletaold_lastBrewLevel = 0;
    @Unique
    private int kaletaold_brewCount = 0;

    // ===== 数据存取 =====
    @Unique
    private int getBrewCount() {
        return kaletaold_brewCount;
    }

    @Unique
    private void setBrewCount(int count) {
        kaletaold_brewCount = count;
        System.out.println("【老牌酒馆】setBrewCount: " + count);
    }

    @Unique
    private void incrementBrewCount() {
        int current = getBrewCount();
        setBrewCount(current + 1);
        System.out.println("【老牌酒馆】✅ 酿造次数增加！当前次数: " + (current + 1));
    }

    @Unique
    private Level getLevelSafe() {
        return ((BlockEntity)(Object)this).getLevel();
    }

    @Unique
    private BlockPos getBlockPosSafe() {
        return ((BlockEntity)(Object)this).getBlockPos();
    }

    // ===== 速度计算 =====
    @Unique
    private int getCurrentSpeedBonus() {
        int maxCount = Config.COMMON.maxBrewCountForSpeed.get();
        int maxSpeed = Config.COMMON.maxSpeedPercent.get();

        int brewCount = getBrewCount();
        if (brewCount <= 0) return 0;

        int effectiveCount = Math.min(brewCount, maxCount);
        double baseSpeed = 0.05;

        double speed;
        if (maxCount == 1) {
            speed = maxSpeed / 100.0;
        } else {
            speed = baseSpeed + (effectiveCount - 1) * ((maxSpeed / 100.0) - baseSpeed) / (maxCount - 1);
        }

        speed = Math.min(speed, maxSpeed / 100.0);
        speed = Math.max(speed, baseSpeed);

        int result = (int) Math.round(speed * 100);
        System.out.println("【老牌酒馆】速度加成: " + result + "%");
        return result;
    }

    // ===== 损坏计算 =====
    @Unique
    private double getDestroyChance() {
        double baseChance = 0.05;
        double perBrewChance = 0.05;
        int brewCount = getBrewCount();
        double totalChance = baseChance + brewCount * perBrewChance;
        return Math.min(totalChance, 0.55);
    }

    @Unique
    private boolean shouldDestroy() {
        return RANDOM.nextDouble() < getDestroyChance();
    }

    // ===== 爆炸特效 =====
    @Unique
    private void destroyBarrel(BlockPos pos) {
        Level level = getLevelSafe();
        if (level == null || level.isClientSide) return;

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
    }

    // ===== 核心：重写 getBrewTimeForLevel，每个阶段单独加速 =====
    @Inject(
            method = "getBrewTimeForLevel",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void onGetBrewTimeForLevel(CallbackInfoReturnable<Integer> cir) {
        Level level = getLevelSafe();
        if (level == null) {
            cir.setReturnValue(BarrelRecipeSerializer.DEFAULT_UNIT_TIME * this.brewLevel);
            return;
        }

        if (this.isMaxBrewLevel()) {
            cir.setReturnValue(-1);
            return;
        }

        // 获取原始时间
        int originalTime;
        if (this.recipeId == null || this.recipeId.equals(BarrelRecipeSerializer.EMPTY_RECIPE_ID)) {
            originalTime = BarrelRecipeSerializer.DEFAULT_UNIT_TIME * this.brewLevel;
        } else {
            originalTime = level.getRecipeManager().byKey(this.recipeId).map(recipe -> {
                if (recipe instanceof BarrelRecipe barrelRecipe) {
                    return barrelRecipe.unitTime() * this.brewLevel;
                }
                return BarrelRecipeSerializer.DEFAULT_UNIT_TIME * this.brewLevel;
            }).orElse(BarrelRecipeSerializer.DEFAULT_UNIT_TIME * this.brewLevel);
        }

        // 每个阶段单独应用速度加成
        int bonus = getCurrentSpeedBonus();
        if (bonus > 0) {
            int newTime = (int) (originalTime * (1 - bonus / 100.0));
            newTime = Math.max(newTime, 1);
            System.out.println("【老牌酒馆】阶段" + this.brewLevel + " 时间: " + originalTime + " → " + newTime + " (加成" + bonus + "%)");
            cir.setReturnValue(newTime);
        }
    }

    // ===== 检测酿造完成 =====
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/github/ysbbbbbb/kaleidoscopetavern/blockentity/brew/BarrelBlockEntity;refresh()V",
                    shift = At.Shift.AFTER,
                    ordinal = 1
            ),
            remap = false
    )
    private void onBrewLevelUp(Level level, CallbackInfo ci) {
        if (level.isClientSide) return;

        int currentLevel = this.getBrewLevel();

        if (currentLevel >= BREWING_FINISHED && kaletaold_lastBrewLevel < BREWING_FINISHED) {
            System.out.println("【老牌酒馆】🎉 酿造完成！brewLevel = " + currentLevel);
            incrementBrewCount();
            BlockPos pos = getBlockPosSafe();
            if (shouldDestroy()) {
                System.out.println("【老牌酒馆】💥 酒桶损坏！");
                destroyBarrel(pos);
            }
        }

        kaletaold_lastBrewLevel = currentLevel;
    }

    // ===== 保存数据 =====
    @Inject(method = "saveAdditional", at = @At("TAIL"), remap = true)
    private void onSaveAdditional(CompoundTag tag, CallbackInfo ci) {
        tag.putInt(BREW_COUNT_KEY, kaletaold_brewCount);
    }

    // ===== 加载数据 =====
    @Inject(method = "load", at = @At("TAIL"), remap = true)
    private void onLoad(CompoundTag tag, CallbackInfo ci) {
        kaletaold_brewCount = tag.getInt(BREW_COUNT_KEY);
    }
}