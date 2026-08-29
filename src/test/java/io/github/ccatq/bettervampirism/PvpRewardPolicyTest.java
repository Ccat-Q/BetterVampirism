package io.github.ccatq.bettervampirism;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PvpRewardPolicyTest {
    @Test
    void acceptsOnlySurvivalAndAdventure() {
        assertTrue(PvpRewardPolicy.isEligibleGameMode("survival"));
        assertTrue(PvpRewardPolicy.isEligibleGameMode("adventure"));
        assertFalse(PvpRewardPolicy.isEligibleGameMode("creative"));
        assertFalse(PvpRewardPolicy.isEligibleGameMode("spectator"));
    }

    @Test
    void acceptsOnlyVampireAndHunterPairs() {
        assertTrue(PvpRewardPolicy.areOpposingPlayableFactions(PvpRewardPolicy.VAMPIRE_FACTION_ID, PvpRewardPolicy.HUNTER_FACTION_ID));
        assertTrue(PvpRewardPolicy.areOpposingPlayableFactions(PvpRewardPolicy.HUNTER_FACTION_ID, PvpRewardPolicy.VAMPIRE_FACTION_ID));
        assertFalse(PvpRewardPolicy.areOpposingPlayableFactions(PvpRewardPolicy.VAMPIRE_FACTION_ID, PvpRewardPolicy.VAMPIRE_FACTION_ID));
        assertFalse(PvpRewardPolicy.areOpposingPlayableFactions(PvpRewardPolicy.VAMPIRE_FACTION_ID, "minecraft:neutral"));
    }

    @Test
    void acceptsOnlyExpiredCooldowns() {
        assertTrue(PvpRewardPolicy.isCooldownExpired(120_000L, 120_000L));
        assertTrue(PvpRewardPolicy.isCooldownExpired(120_001L, 120_000L));
        assertFalse(PvpRewardPolicy.isCooldownExpired(119_999L, 120_000L));
    }
}
