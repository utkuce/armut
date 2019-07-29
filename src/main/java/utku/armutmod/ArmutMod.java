package utku.armutmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ArmutMod.MODID, name = ArmutMod.NAME, version = ArmutMod.VERSION)
public class ArmutMod implements IProxy {

    @SidedProxy(clientSide = "utku.armutmod.ClientCode", serverSide = "utku.armutmod.ServerCode")
    private static ArmutMod proxy;

    public static final String MODID = "armutmod";
    public static final String NAME = "Armut Mod";
    public static final String VERSION = "0.1.0";

    protected static Logger logger;
    protected static MyLogger mylogger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }
}
