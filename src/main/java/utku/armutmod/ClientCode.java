package utku.armutmod;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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
    private Configuration config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        mylogger = new MyLogger(logger);

        //this line either creates the file if it doesn't exist or opens it if it already exists.
        config = new Configuration(new File("config/" + ArmutMod.MOD_ID + ".cfg"));
        config.load();

        setServerAddress();

        if (serverAddress.isEmpty()) {
            mylogger.info("No server address set");
            return;
        }

        clientCode(); // will only run on the client
        mylogger.close();
    }

    private void setServerAddress() {

        serverAddress = config.get(Configuration.CATEGORY_CLIENT, "armutServer", "127.0.0.1").getString();
        serverAddress = serverAddress + ":" + DEFAULT_PORT;
    }

    private void downloadFile(String remotePath) {

        mylogger.info("Downloading file: " + serverAddress + "/" + remotePath);
        mylogger.info("to " + System.getProperty("user.dir") + remotePath);
        mylogger.info(System.lineSeparator());

        try {

            FileUtils.copyURLToFile(

                    new URL("http://" + serverAddress + "/" + remotePath), // minecraft.server/mods/modname.jar
                    new File(remotePath) // .minecraft/mods/modname.jar
            );

        } catch (IOException e) {
            e.printStackTrace();
            mylogger.info(e.getMessage());
        }
    }

    @SideOnly(Side.CLIENT)
    private void clientCode() {
        logger.info("Running in client mode");
        mylogger.info("Armut pis agzima dus");

        mylogger.info(System.lineSeparator());
        mylogger.info("Syncing mod files from " + serverAddress);
        syncFiles("armut/mods_list.txt");

        boolean downloadConfigs =
                config.get(Configuration.CATEGORY_CLIENT, "downloadModConfigs", false)
                        .getBoolean();

        if (downloadConfigs) {

            mylogger.info(System.lineSeparator());
            mylogger.info("Syncing config files from " + serverAddress);
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

                mylogger.info("Checking file: " + fileName);
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
                    mylogger.info("File not found, downloading...");
                    downloadFile(fileName);
                }

            }
            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
            mylogger.info(e.getMessage());
        }
    }

    /*
    @Mod.EventBusSubscriber
    public class MyForgeEventHandler {

        @SubscribeEvent
        public void connectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {

            mylogger = new MyLogger(logger);
            mylogger.info("Client connected to server " + event.isLocal());
            mylogger.close();
        }
    }*/
}

