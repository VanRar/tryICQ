import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Client {
    public static String host;
    public static int port;
    protected static final Logger LOGGER = Logger.getLogger("ClientLogger");

    public static void main(String[] args) {

        //Выбор имени для участия в чате;+
        //Прочитать настройки приложения из файла настроек - например, номер порта сервера;+
        //Подключение к указанному в настройках серверу;+
        //Для выхода из чата нужно набрать команду выхода - “/exit”;+
        //Каждое сообщение участников должно записываться в текстовый файл - файл логирования. При каждом запуске приложения файл должен дополняться.+

        //запускаем logger
        setupLogger();
        //Прочитать настройки приложения из файла настроек - например, номер порта сервера;
        clientSettings();
        //и запускаем циклы прослушивания и отправки в многопоточном режиме
        new ClientMultiThread(host, port);
    }

    public static void setupLogger() {
        LogManager.getLogManager().reset();//сбрасываем настройки, можно методом отключения

        /*
        устанавливаем уровни логирования
         Перечень уровней по порядку.
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

        LOGGER.setLevel(Level.FINE);
        //создаем обработчик логов, мы будем писать логи в файл
        try {
            FileHandler fileHandler = new FileHandler("Client/src/main/resources/client.log", true); //true - продолжаем запись
            fileHandler.setLevel(Level.FINE);//прописываем уровень
            LOGGER.addHandler(fileHandler);//добавляем файловый обработчик в logger
            LOGGER.log(Level.CONFIG, "Logger запущен");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clientSettings() {
        Properties settings = new Properties();
        try {
            //выгрузим файл настроек
            settings.load(new FileInputStream("src/main/resources/settings.properties"));
            System.out.println(settings);
            //прочитаем требуемые значения
            host = settings.getProperty("host");
            System.out.println("Хост сервера: " + host);
            port = Integer.parseInt(settings.getProperty("port"));
            System.out.println("Порт сервера: " + port);
            LOGGER.log(Level.CONFIG, "Файл настроек найден, настройки загружены");
        } catch (IOException e) {
            System.out.println("Файл настроек не найден");
            LOGGER.log(Level.CONFIG, "Файл настроек не найден");
            //можно добавить запись настроек в ручном режиме, но тут не критично уже.
        }
    }

    public static String getCurrentDate() {
        Date time;
        String dTime;
        SimpleDateFormat dateFormat;

        time = new Date();//выставляем текущую дату
        dateFormat = new SimpleDateFormat("HH:mm:ss"); //устанавливаем формат даты
        dTime = dateFormat.format(time);
        return " { " + dTime + " } ";
    }
}