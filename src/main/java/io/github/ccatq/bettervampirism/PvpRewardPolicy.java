package io.github.ccatq.bettervampirism;


public final class PvpRewardPolicy {
    public static final String VAMPIRE_FACTION_ID = "vampirism:vampire";
    public static final String HUNTER_FACTION_ID = "vampirism:hunter";

    private PvpRewardPolicy() {
    }

    public static boolean isEligibleGameMode(String gameModeName) {
        return "survival".equals(gameModeName) || "adventure".equals(gameModeName);
    }

    public static boolean areOpposingPlayableFactions(String killerFactionId, String victimFactionId) {
        return (VAMPIRE_FACTION_ID.equals(killerFactionId) && HUNTER_FACTION_ID.equals(victimFactionId))
                || (HUNTER_FACTION_ID.equals(killerFactionId) && VAMPIRE_FACTION_ID.equals(victimFactionId));
    }

    public static boolean isCooldownExpired(long nowMillis, long expiresAtMillis) {
        return nowMillis >= expiresAtMillis;
    }
}

