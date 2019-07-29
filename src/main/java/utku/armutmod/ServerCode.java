package utku.armutmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ServerCode extends ArmutMod implements IProxy{

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        mylogger = new MyLogger(logger);
        serverCode(); // will only run on the client
        mylogger.close();
    }

    @SideOnly(Side.SERVER)
    private void serverCode(){
        logger.info("Running in server mode");
    }
}
