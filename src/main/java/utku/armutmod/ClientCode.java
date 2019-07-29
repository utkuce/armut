package utku.armutmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;

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

    @SideOnly(Side.CLIENT)
    private void clientCode() {
        logger.info("Running in client mode");

        setServerAddress();

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
    }
}
