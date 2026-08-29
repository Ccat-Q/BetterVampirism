package io.github.ccatq.bettervampirism;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;

public final class PvpRewardPolicy {
    public static final ResourceLocation VAMPIRE_FACTION = ResourceLocation.fromNamespaceAndPath("vampirism", "vampire");
    public static final ResourceLocation HUNTER_FACTION = ResourceLocation.fromNamespaceAndPath("vampirism", "hunter");

    private PvpRewardPolicy() {
    }

    public static boolean isEligibleGameMode(GameType gameType) {
        return gameType == GameType.SURVIVAL || gameType == GameType.ADVENTURE;
    }

    public static boolean areOpposingPlayableFactions(ResourceLocation killerFaction, ResourceLocation victimFaction) {
        return (VAMPIRE_FACTION.equals(killerFaction) && HUNTER_FACTION.equals(victimFaction))
                || (HUNTER_FACTION.equals(killerFaction) && VAMPIRE_FACTION.equals(victimFaction));
    }

    public static boolean isCooldownExpired(long nowMillis, long expiresAtMillis) {
        return nowMillis >= expiresAtMillis;
    }
}

