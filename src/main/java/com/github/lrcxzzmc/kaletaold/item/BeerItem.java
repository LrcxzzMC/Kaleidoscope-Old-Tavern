package com.github.lrcxzzmc.kaletaold.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BeerItem extends Item {
    public BeerItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        stack = super.finishUsingItem(stack, level, entity);

        if (entity instanceof Player player && !player.isCreative()) {
            if (!player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE))) {
                player.spawnAtLocation(Items.GLASS_BOTTLE);
            }
        }

        return stack;
    }
}