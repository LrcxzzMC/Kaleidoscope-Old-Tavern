package com.github.lrcxzzmc.kaletaold;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec SPEC;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair =
                new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON = specPair.getLeft();
        SPEC = specPair.getRight();
    }

    public static class CommonConfig {
        public final ForgeConfigSpec.IntValue maxBrewCountForSpeed;
        public final ForgeConfigSpec.IntValue maxSpeedPercent;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("森罗物语：老牌酒馆 - 酿酒桶加速配置")
                    .push("brew_speed");

            maxBrewCountForSpeed = builder
                    .comment("达到最大速度加成所需的酿酒次数（默认5次，范围1~100）")
                    .defineInRange("maxBrewCountForSpeed", 5, 1, 100);

            maxSpeedPercent = builder
                    .comment("最大速度加成百分比（默认25%，范围1~100）")
                    .defineInRange("maxSpeedPercent", 25, 1, 100);  // ← 改成 25

            builder.pop();
        }
    }
}