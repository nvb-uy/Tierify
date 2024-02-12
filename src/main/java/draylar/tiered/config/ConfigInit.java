package draylar.tiered.config;

import elocindev.necronomicon.api.config.v1.NecConfigAPI;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

public class ConfigInit {
    public static CommonConfig CONFIG = new CommonConfig();
    public static TieredConfig CLIENT_CONFIG = new TieredConfig();

    public static void init() {
        AutoConfig.register(TieredConfig.class, JanksonConfigSerializer::new);
        NecConfigAPI.registerConfig(CommonConfig.class);

        CONFIG = CommonConfig.INSTANCE;
        CLIENT_CONFIG = AutoConfig.getConfigHolder(TieredConfig.class).getConfig();
    }

}
