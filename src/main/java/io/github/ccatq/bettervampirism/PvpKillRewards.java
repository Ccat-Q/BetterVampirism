package io.github.ccatq.bettervampirism;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PvpKillRewards {
    private static final ResourceLocation HUMAN_HEART = ResourceLocation.fromNamespaceAndPath("vampirism", "human_heart");
    private static final ResourceLocation VAMPIRE_FANG = ResourceLocation.fromNamespaceAndPath("vampirism", "vampire_fang");
    private static final ResourceLocation BLOOD_BOTTLE = ResourceLocation.fromNamespaceAndPath("vampirism", "blood_bottle");
    private static final ResourceLocation VAMPIRE_BLOOD_BOTTLE = ResourceLocation.fromNamespaceAndPath("vampirism", "vampire_blood_bottle");
    private static final Map<KillPair, Long> COOLDOWNS = new HashMap<>();

    private PvpKillRewards() {
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide() || !(event.getEntity() instanceof ServerPlayer victim)) {
            return;
        }

        ServerPlayer killer = resolveKiller(event);
        if (killer == null || killer == victim || !isEligiblePlayer(killer) || !isEligiblePlayer(victim)) {
            return;
        }

        IFactionPlayerHandler killerHandler = VampirismAPI.factionPlayerHandler(killer);
        IFactionPlayerHandler victimHandler = VampirismAPI.factionPlayerHandler(victim);
        IPlayableFaction<?> killerFaction = killerHandler.getCurrentFaction();
        IPlayableFaction<?> victimFaction = victimHandler.getCurrentFaction();
        if (killerFaction == null || victimFaction == null
                || !PvpRewardPolicy.areOpposingPlayableFactions(killerFaction.getID(), victimFaction.getID())) {
            return;
        }

        KillPair pair = new KillPair(killer.getUUID(), victim.getUUID());
        long nowMillis = System.currentTimeMillis();
        long expiresAtMillis = COOLDOWNS.getOrDefault(pair, 0L);
        if (!PvpRewardPolicy.isCooldownExpired(nowMillis, expiresAtMillis)) {
            return;
        }

        increaseLevel(killerHandler, killerFaction);
        if (PvpRewardPolicy.VAMPIRE_FACTION.equals(killerFaction.getID())) {
            drop(victim, HUMAN_HEART, BetterVampirismConfig.VAMPIRE_KILLS_HUNTER);
        } else {
            drop(victim, VAMPIRE_FANG, BetterVampirismConfig.HUNTER_KILLS_VAMPIRE_FANG);
            drop(victim, BLOOD_BOTTLE, BetterVampirismConfig.HUNTER_KILLS_BLOOD);
            drop(victim, VAMPIRE_BLOOD_BOTTLE, BetterVampirismConfig.HUNTER_KILLS_VAMPIRE_BLOOD);
        }

        long cooldownMillis = (long) BetterVampirismConfig.PAIR_COOLDOWN_SECONDS.getAsInt() * 1_000L;
        COOLDOWNS.put(pair, nowMillis + cooldownMillis);
    }

    private static ServerPlayer resolveKiller(LivingDeathEvent event) {
        Entity responsible = event.getSource().getEntity();
        if (responsible instanceof ServerPlayer player) {
            return player;
        }
        Entity direct = event.getSource().getDirectEntity();
        if (direct instanceof Projectile projectile && projectile.getOwner() instanceof ServerPlayer player) {
            return player;
        }
        return null;
    }

    private static boolean isEligiblePlayer(ServerPlayer player) {
        GameType gameType = player.gameMode.getGameModeForPlayer();
        return PvpRewardPolicy.isEligibleGameMode(gameType);
    }

    private static void increaseLevel(IFactionPlayerHandler handler, IPlayableFaction<?> faction) {
        int currentLevel = handler.getCurrentLevel(faction);
        if (currentLevel < faction.getHighestReachableLevel()) {
            handler.setFactionLevel(faction, currentLevel + 1);
        }
    }

    private static void drop(ServerPlayer victim, ResourceLocation itemId, BetterVampirismConfig.RewardConfig reward) {
        if (!reward.enabled() || reward.count() == 0 || victim.getRandom().nextDouble() > reward.chance()) {
            return;
        }
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null || item == net.minecraft.world.item.Items.AIR) {
            BetterVampirism.LOGGER.warn("Configured Vampirism reward item {} is not registered", itemId);
            return;
        }
        ItemEntity itemEntity = new ItemEntity(victim.level(), victim.getX(), victim.getY(), victim.getZ(), new ItemStack(item, reward.count()));
        victim.level().addFreshEntity(itemEntity);
    }

    private record KillPair(UUID killer, UUID victim) {
    }
}

