import java.io.IOException;
import java.util.logging.*;

public class TestLoggerFile {
    private static final Logger LOGGER = Logger.getLogger("TeatLoggerFile");

    public static void main(String[] args) {

        //получаем диспетчер логов и сбрасываем настройки
        LogManager.getLogManager().reset();

        /*
         Different Levels in order.
          OFF
          SEVERE
          WARNING
          INFO
          CONFIG
          FINE
          FINER
          FINEST
          ALL
        */

        LOGGER.setLevel(Level.FINE); //устанавливаем уровни

        //создаем обработчиков логов, их может быть несколько
        //по сути консольный нам не нужен
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL); //устанавливаем уровни для обработчика
        LOGGER.addHandler(consoleHandler);//добавляем обработчика в logger

        try {
            FileHandler fileHandler = new FileHandler("src/main/resources/loggerFile.log", true);
            fileHandler.setLevel(Level.FINE);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }


        LOGGER.log(Level.INFO, "Лог на уровне инфо");
        LOGGER.log(Level.FINE, "Лог на уровне fine");
        LOGGER.log(Level.FINER, "Лог на уровне finer");
        LOGGER.log(Level.FINEST, "Лог на уровне finest");
        LOGGER.log(Level.CONFIG, "Лог на уровне config");
        LOGGER.log(Level.WARNING, "Лог на уровне warning");
    }
    //  Handler loggerHandler;


//        try {
//            loggerHandler = new FileHandler("%h/loggerTest.log");
//            loggerHandler.
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


}

