package utku.armutmod;

import net.minecraftforge.common.config.Config;

@Config(modid = ArmutMod.MOD_ID, name = ArmutMod.MOD_ID, category = "client")
public class ArmutConfig {

    @Config.Comment({
            "Address of a server that has also armut mod installed to download mods from"
           // "- For multiple servers you may supply an array",
           // "- If a non default port is provided by the server admin it can be appended to the server address (<server_address>:<port_number>)"
    })
    @Config.RequiresMcRestart
    public static String armutServer = "127.0.0.1";

    @Config.Comment({"When set to true config files of the downloaded mods will also be downloaded every time minecraft is launched ",
                     "Note that this will override any config files you may have changed"
    })
    public static Boolean downloadModConfigs = false;
}