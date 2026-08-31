package io.github.ccatq.bettervampirism;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.ICaptureIgnore;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.world.ITotem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/** Reports the active-capture participants when a player rings a bell. */
public final class VillageRaidBellStatus {
    private VillageRaidBellStatus() {
    }

    public static void onRightClickBell(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !(event.getEntity() instanceof ServerPlayer player)
                || !level.getBlockState(event.getPos()).is(Blocks.BELL)) {
            return;
        }

        findNearbyTotem(level, player.blockPosition())
                .ifPresent(totem -> reportActiveRaid(player, totem));
    }

    private static Optional<ITotem> findNearbyTotem(ServerLevel level, BlockPos center) {
        ITotem nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-25, -16, -25), center.offset(25, 16, 25))) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof ITotem totem)) {
                continue;
            }
            double distance = pos.distSqr(center);
            if (distance < nearestDistance) {
                nearest = totem;
                nearestDistance = distance;
            }
        }
        return Optional.ofNullable(nearest);
    }

    private static void reportActiveRaid(ServerPlayer player, ITotem totem) {
        IFaction<?> attackers = totem.getCapturingFaction();
        IFaction<?> defenders = totem.getControllingFaction();
        if (attackers == null) {
            player.sendSystemMessage(Component.literal("附近村庄当前没有进行中的袭击。"));
            return;
        }

        Map<String, Integer> attackerEntities = new TreeMap<>();
        Map<String, Integer> defenderEntities = new TreeMap<>();
        for (LivingEntity entity : player.serverLevel().getEntitiesOfClass(LivingEntity.class, totem.getVillageArea())) {
            if (!entity.isAlive() || entity instanceof ICaptureIgnore) {
                continue;
            }
            IFaction<?> faction = VampirismAPI.factionRegistry().getFaction(entity);
            if (attackers.equals(faction)) {
                attackerEntities.merge(entity.getDisplayName().getString(), 1, Integer::sum);
            } else if (defenders != null && defenders.equals(faction)) {
                defenderEntities.merge(entity.getDisplayName().getString(), 1, Integer::sum);
            }
        }

        player.sendSystemMessage(Component.literal("袭击未解决单位 - 攻击方 "
                + format(attackerEntities) + "；防守方 " + format(defenderEntities)));
    }

    private static String format(Map<String, Integer> entities) {
        if (entities.isEmpty()) {
            return "无";
        }
        return entities.entrySet().stream()
                .map(entry -> entry.getKey() + " ×" + entry.getValue())
                .reduce((left, right) -> left + "、" + right)
                .orElse("无");
    }
}
