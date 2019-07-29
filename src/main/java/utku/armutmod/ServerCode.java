package utku.armutmod;

import fi.iki.elonen.SimpleWebServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

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
        logger.info("Armut pis agzima dus");

        //SimpleWebServer.main(null);

        try {

            new File("armut").mkdir();
            FileWriter modsListTxt = new FileWriter("armut/mods_list.txt");
            FileWriter configsListTxt = new FileWriter("armut/configs_list.txt");

            mylogger.info("Writing mods to armut/mods_list.txt");
            listDirectoryToFile("mods", modsListTxt);

            mylogger.info("Writing configs to armut/configs_list.txt");
            listDirectoryToFile("config", configsListTxt);

            modsListTxt.close();
            configsListTxt.close();

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
                outputFile.write( fileFullName + System.lineSeparator());
                mylogger.info(" Writing file name " + fileFullName);

            } else if (file.isDirectory()) {
                listDirectoryToFile(folderName + "/" + file.getName(), outputFile);
            }
        }
    }
}
