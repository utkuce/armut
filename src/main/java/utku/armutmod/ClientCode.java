package utku.armutmod;

import jdk.nashorn.internal.parser.JSONParser;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class ClientCode extends ArmutMod implements IProxy {

    private String serverAddress = "localhost:27688";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        mylogger = new MyLogger(logger);
        clientCode(); // will only run on the client
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
                mylogger.info(e.getMessage());
            }
        } else {

            try {
                new File(serverListFile).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                mylogger.info(e.getMessage());
            }
        }
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

    private static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    @SideOnly(Side.CLIENT)
    private void clientCode() {
        logger.info("Running in client mode");

        setServerAddress();
        mylogger.info("Armut pis agzima dus");

        mylogger.info(System.lineSeparator());
        mylogger.info("Syncing mod files from " + serverAddress);
        syncFiles("armut/mods_list.txt");

        mylogger.info(System.lineSeparator());
        mylogger.info("Syncing config files from " + serverAddress);
        syncFiles("armut/configs_list.txt");
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
}
