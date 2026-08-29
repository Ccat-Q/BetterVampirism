package io.github.ccatq.bettervampirism;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PvpRewardPolicyTest {
    @Test
    void acceptsOnlySurvivalAndAdventure() {
        assertTrue(PvpRewardPolicy.isEligibleGameMode(GameType.SURVIVAL));
        assertTrue(PvpRewardPolicy.isEligibleGameMode(GameType.ADVENTURE));
        assertFalse(PvpRewardPolicy.isEligibleGameMode(GameType.CREATIVE));
        assertFalse(PvpRewardPolicy.isEligibleGameMode(GameType.SPECTATOR));
    }

    @Test
    void acceptsOnlyVampireAndHunterPairs() {
        assertTrue(PvpRewardPolicy.areOpposingPlayableFactions(PvpRewardPolicy.VAMPIRE_FACTION, PvpRewardPolicy.HUNTER_FACTION));
        assertTrue(PvpRewardPolicy.areOpposingPlayableFactions(PvpRewardPolicy.HUNTER_FACTION, PvpRewardPolicy.VAMPIRE_FACTION));
        assertFalse(PvpRewardPolicy.areOpposingPlayableFactions(PvpRewardPolicy.VAMPIRE_FACTION, PvpRewardPolicy.VAMPIRE_FACTION));
        assertFalse(PvpRewardPolicy.areOpposingPlayableFactions(PvpRewardPolicy.VAMPIRE_FACTION, ResourceLocation.fromNamespaceAndPath("minecraft", "neutral")));
    }

    @Test
    void acceptsOnlyExpiredCooldowns() {
        assertTrue(PvpRewardPolicy.isCooldownExpired(120_000L, 120_000L));
        assertTrue(PvpRewardPolicy.isCooldownExpired(120_001L, 120_000L));
        assertFalse(PvpRewardPolicy.isCooldownExpired(119_999L, 120_000L));
    }
}
