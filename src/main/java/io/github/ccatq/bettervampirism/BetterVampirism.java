package io.github.ccatq.bettervampirism;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(BetterVampirism.MOD_ID)
public final class BetterVampirism {
    public static final String MOD_ID = "bettervampirism";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BetterVampirism(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, BetterVampirismConfig.SPEC);
        NeoForge.EVENT_BUS.addListener(PvpKillRewards::onLivingDeath);
    }
}

