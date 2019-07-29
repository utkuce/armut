package utku.armutmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

@Mod(modid = ArmutMod.MODID, name = ArmutMod.NAME, version = ArmutMod.VERSION)
public class ArmutMod
{
    public static final String MODID = "armutmod";
    public static final String NAME = "Armut Mod";
    public static final String VERSION = "0.1";

    private static Logger logger;
    private static MyLogger mylogger;

    private String serverAddress = "localhost";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {

        setServerAddress();

        mylogger = new MyLogger(logger);

        mylogger.info("Armut pis agzima dus");
        mylogger.info("Getting list of mods from " + serverAddress);

        try {
            URL url = new URL("http://" + serverAddress + "/mods_list.txt");
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                String modName = scanner.next();

                mylogger.info("Checking mod: " + modName);
                if (Files.exists(Paths.get("mods/" + modName))) {
                    mylogger.info("Mod exists, skipping download");
                }
                else {
                    downloadMod(modName);
                }

            }
            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        mylogger.close();
    }

    private void setServerAddress() {

        String serverListFile = "config/armut.cfg";
        if (Files.exists(Paths.get(serverListFile))) {

            try {
                String readAddress = readFile(serverListFile, StandardCharsets.US_ASCII);
                if (!readAddress.isEmpty()) {
                    serverAddress = readAddress;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            try {
                new File(serverListFile).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadMod(String modName) {

        mylogger.info("Downloading mod: " + modName);

        try {

            FileUtils.copyURLToFile(

                    new URL("http://" + serverAddress + "/mods/" + modName),
                    new File("mods/" + modName)
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
