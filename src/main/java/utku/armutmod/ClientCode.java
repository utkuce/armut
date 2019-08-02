package utku.armutmod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class ClientCode extends ArmutMod implements IProxy {

    private String serverAddress;
    final private static short DEFAULT_PORT = 27688;
    private static Configuration config;
    final private static String configPath = "config/" + ArmutMod.MOD_ID + ".cfg";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        config = new Configuration(new File(configPath));
        setServerAddress();

        if (serverAddress.isEmpty()) {
            logger.info("No server address set");
            return;
        }

        clientCode();
    }

    private void setServerAddress() {

        serverAddress = getStringConfig(configPath, Configuration.CATEGORY_CLIENT, "armutServer");
        serverAddress = serverAddress + ":" + DEFAULT_PORT;
    }

    private void downloadFile(String remotePath) {

        logger.info("Downloading file: " + serverAddress + "/" + remotePath +
                        "to " + System.getProperty("user.dir") + remotePath);

        try {

            FileUtils.copyURLToFile(

                    new URL("http://" + serverAddress + "/" + remotePath), // minecraft.server/mods/modname.jar
                    new File(remotePath) // .minecraft/mods/modname.jar
            );

        } catch (IOException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
    }

    @SideOnly(Side.CLIENT)
    private void clientCode() {
        logger.info("Running in client mode");
        logger.info("Armut pis agzima dus");

        logger.info("Syncing mod files from " + serverAddress);
        syncFiles("armut/mods_list.txt");

        boolean downloadConfigs = getBooleanConfig(configPath, Configuration.CATEGORY_CLIENT, "downloadModConfigs");

        logger.info("downloadConfigs=" + downloadConfigs);

        if (downloadConfigs) {

            logger.info("Syncing config files from " + serverAddress);
            syncFiles("armut/configs_list.txt");
        }


    }

    private void syncFiles(String listPath) {

        try {

            URL url = new URL("http://" + serverAddress + "/" + listPath);
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNextLine()) {

                Object obj= JSONValue.parse(scanner.nextLine());
                JSONObject jsonObject = (JSONObject) obj;

                String fileName = (String) jsonObject.get("path");
                Long lastModified = (Long) jsonObject.get("lastModified");

                logger.info("Checking file: " + fileName);
                File f = new File(fileName);
                if (f.exists()) {
/*
                    if (f.getAbsoluteFile().lastModified() < lastModified) {
                        mylogger.info("Local file is older than server's");
                        downloadFile(fileName);
                    }

 */
                }
                else {
                    logger.info("File not found, downloading...");
                    downloadFile(fileName);
                }

            }
            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
    }

    @Mod.EventBusSubscriber
    public static class MyForgeEventHandler {

        @SubscribeEvent
        public static void connectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {

            logger.info("Client connected to server, (local:" + event.isLocal() +
                    ", server address: " + Minecraft.getMinecraft().getCurrentServerData().serverIP + ")");
        }

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(MOD_ID)) {

                logger.info("Mod config changed");

                getBooleanConfig(configPath, Configuration.CATEGORY_CLIENT, "downloadModConfigs");
                getStringConfig(configPath, Configuration.CATEGORY_CLIENT, "armutServer");

                config.save();
            }
        }
    }

    private static String getStringConfig(String configPath, String category, String key) {
        config = new Configuration(new File(configPath));
        try {
            config.load();
            if (config.getCategory(category).containsKey(key)) {
                return config.get(category, key, "").getString();
            }
        } catch (Exception e) {
            System.out.println("Cannot load configuration file!");
        } finally {
            config.save();
        }
        return "";
    }

    private static Boolean getBooleanConfig(String configPath, String category, String key) {
        config = new Configuration(new File(configPath));
        try {
            config.load();
            if (config.getCategory(category).containsKey(key)) {
                return config.get(category, key, false).getBoolean();
            }
        } catch (Exception e) {
            System.out.println("Cannot load configuration file!");
        } finally {
            config.save();
        }
        return false;
    }


}

