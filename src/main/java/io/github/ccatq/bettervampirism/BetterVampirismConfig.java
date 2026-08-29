package io.github.ccatq.bettervampirism;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class BetterVampirismConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue PAIR_COOLDOWN_SECONDS = BUILDER
            .comment("Real-time cooldown for the same killer-victim UUID pair. It resets when the server restarts.")
            .defineInRange("pairCooldownSeconds", 120, 0, 86_400);

    public static final RewardConfig VAMPIRE_KILLS_HUNTER = new RewardConfig(
            "vampireKillsHunter",
            "Human heart reward when a vampire player kills a hunter player.");
    public static final RewardConfig HUNTER_KILLS_VAMPIRE_FANG = new RewardConfig(
            "hunterKillsVampireFang",
            "Vampire fang reward when a hunter player kills a vampire player.");
    public static final RewardConfig HUNTER_KILLS_VAMPIRE_BLOOD = new RewardConfig(
            "hunterKillsVampireBloodBottle",
            "Vampire blood bottle reward when a hunter player kills a vampire player.");
    public static final RewardConfig HUNTER_KILLS_BLOOD = new RewardConfig(
            "hunterKillsBloodBottle",
            "Ordinary blood bottle reward when a hunter player kills a vampire player.");

    public static final ModConfigSpec SPEC = BUILDER.build();

    private BetterVampirismConfig() {
    }

    public static final class RewardConfig {
        private final ModConfigSpec.BooleanValue enabled;
        private final ModConfigSpec.IntValue count;
        private final ModConfigSpec.DoubleValue chance;

        private RewardConfig(String path, String comment) {
            BUILDER.push(path);
            enabled = BUILDER.comment(comment).define("enabled", true);
            count = BUILDER.comment("Number of items dropped when this reward succeeds.")
                    .defineInRange("count", 1, 0, 64);
            chance = BUILDER.comment("Drop chance from 0.0 to 1.0.")
                    .defineInRange("chance", 1.0D, 0.0D, 1.0D);
            BUILDER.pop();
        }

        public boolean enabled() {
            return enabled.get();
        }

        public int count() {
            return count.get();
        }

        public double chance() {
            return chance.get();
        }
    }
}

