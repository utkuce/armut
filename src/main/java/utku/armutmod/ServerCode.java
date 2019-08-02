package utku.armutmod;

import fi.iki.elonen.SimpleWebServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ServerCode extends ArmutMod implements IProxy{

    final private static int PORT = 27688;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        serverCode();
    }

    @SideOnly(Side.SERVER)
    private void serverCode(){
        logger.info("Running in server mode");
        logger.info("Armut pis agzima dus");

        try {

            new File("armut").mkdir();
            FileWriter modsListTxt = new FileWriter("armut/mods_list.txt");
            FileWriter configsListTxt = new FileWriter("armut/configs_list.txt");

            logger.info("Writing mods to armut/mods_list.txt");
            listDirectoryToFile("mods", modsListTxt);

            logger.info("Writing configs to armut/configs_list.txt");
            listDirectoryToFile("config", configsListTxt);

            modsListTxt.close();
            configsListTxt.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            logger.info("Starting web server on port " + PORT);
            new SimpleWebServer("0.0.0.0", PORT, new File(System.getProperty("user.dir")), true).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listDirectoryToFile(String folderName, FileWriter outputFile) throws IOException {

        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null)
            return;

        for (File file : listOfFiles) {

            if (file.isFile()) {

                String fileFullName = folderName + "/" + file.getName();

                JSONObject obj = new JSONObject();
                obj.put("path", fileFullName);
                obj.put("modified", file.lastModified());
                outputFile.write(obj  + System.lineSeparator());

                //outputFile.write(fileFullName + System.lineSeparator());
                logger.info(" Writing file name " + fileFullName);

            } else if (file.isDirectory()) {
                listDirectoryToFile(folderName + "/" + file.getName(), outputFile);
            }
        }
    }
}
