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
        //запускаем logger
        setupLogger();
        //Прочитать настройки приложения из файла настроек - например, номер порта сервера;
        clientSettings();
        //и запускаем циклы прослушивания и отправки в многопоточном режиме
        new ClientMultiThread(host, port);

        //Для выхода из чата нужно набрать команду выхода - “/exit”;
        //Каждое сообщение участников должно записываться в текстовый файл - файл логирования. При каждом запуске приложения файл должен дополняться.
    }

    public static void setupLogger(){
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
            //можно добавить запись настроек в руном режиме, но тут не критично уже.
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

/*попытка номер раз*/
//        //Подключение к указанному в настройках серверу;
//        try (Socket socket = new Socket(host, port);
//             BufferedWriter writer =
//                     new BufferedWriter(
//                             new OutputStreamWriter(
//                                     socket.getOutputStream()));
//             BufferedReader reader =
//                     new BufferedReader(
//                             new InputStreamReader(
//                                     socket.getInputStream()))) {
//
//            System.out.println("Вы вошли в чат");
//            //здесь бесконечный цикл отправки и получения сообщений
//            //хотя, наверное, надо сделать что-то типа ожидания и получения, так как сервак будет рассылать сообщения
//            String request;
//            String response;
//            while (true) {
//                System.out.print(">>");
//                request = scanner.nextLine();
//                //отправим сообщение
//                writer.write(request);
//                writer.newLine();
//                writer.flush();
//                if ("/exit".equals(request)) {
//                    System.out.println("Вы вышли из чата");
//                    break;
//                }
//                //получим ответ
//                response = reader.readLine();
//                System.out.println("Response: " + response);
//            }
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        //по сути у клиента  в большей степени такой же функционал как у сервера
//
//        //Выбор имени для участия в чате;
//        System.out.println("Введите своё имя");
//        name = scanner.nextLine();
