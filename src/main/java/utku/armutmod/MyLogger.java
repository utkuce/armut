package utku.armutmod;

import java.io.FileWriter;
import java.io.IOException;
import org.apache.logging.log4j.Logger;

// same as logger but also writes to a file at the same time
class MyLogger {

    private FileWriter fw;
    private Logger logger;

    MyLogger(Logger logger) {

        this.logger = logger;

        try {
            fw = new FileWriter("logs/armutmod.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void info(String message) {

        logger.info(message);

        try {
            fw.write(message + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void close() {

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
