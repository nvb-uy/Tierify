package draylar.tiered.config;

import elocindev.necronomicon.api.config.v1.NecConfigAPI;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

public class ConfigInit {
    public static CommonConfig CONFIG = new CommonConfig();
    public static ClientConfig CLIENT_CONFIG = new ClientConfig();

    public static void init() {
        AutoConfig.register(ClientConfig.class, JanksonConfigSerializer::new);
        NecConfigAPI.registerConfig(CommonConfig.class);

        CONFIG = CommonConfig.INSTANCE;
        CLIENT_CONFIG = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
    }

}
