import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Server {
    public static Scanner scanner = new Scanner(System.in);
    public static int port;
    protected static final Logger LOGGER = Logger.getLogger("ServerLogger");
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss"); //устанавливаем формат даты

    //список подключений с потоками ввода и вывода, по сути сервер для каждого клиента
   // public static LinkedList<ServerForClientInMultiThread> clientList = new LinkedList<>();
    //заменим на потокобезопасную, не могу пока аргументированно сказать почему именно такая, кроме как то что она
    // потокобезопасная и по нааполнению мне подходит, по сути сортировка мне тут не требуется, но пусть будет такая
    protected static ConcurrentSkipListSet<ServerForClientInMultiThread> clientListCSLM = new ConcurrentSkipListSet<>();

    public static void main(String[] args) {

        //Установка порта для подключения клиентов через файл настроек (например, settings.txt);+
        //Возможность подключиться к серверу в любой момент и присоединиться к чату;+
        //Отправка новых сообщений клиентам;+
        //Запись всех отправленных через сервер сообщений с указанием имени пользователя и времени отправки.+

        setupLogger();
        //проверяем наличие настроек сервера и заполняем их
        installationSettingsProperties();
        //запускаем сервер и ждем подключения клиентов в многопоточном режиме
        startServer();

    }
    public static void setupLogger(){
        LogManager.getLogManager().reset();//сбрасываем настройки, можно методом отключения
        LOGGER.setLevel(Level.FINE);
        //создаем обработчик логов, мы будем писать логи в файл
        try {
            FileHandler fileHandler = new FileHandler("Server/src/main/resources/server.log", true); //true - продолжаем запись
            fileHandler.setLevel(Level.FINE);//прописываем уровень
            LOGGER.addHandler(fileHandler);//добавляем файловый обработчик в logger
            LOGGER.log(Level.CONFIG, "Logger запущен");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void installationSettingsProperties() {
        boolean thereAreSettings = false;
        Properties settings = new Properties();
        try {
            //выгрузим файл настроек
            settings.load(new FileInputStream("src/main/resources/settings.properties"));
            //прочитаем требуемые значения
            //выведем настройки в консоль для пользователя, запускающего сервер
            System.out.println("Порт сервера: " + settings.getProperty("port"));
            System.out.println("Хост: " + settings.getProperty("host"));
            thereAreSettings = true;
            LOGGER.log(Level.CONFIG, "Настройки загружены");
        } catch (IOException e) {
            System.out.println("Файл настроек не найден");//как то не оч логично потом спрашивать, но пусть пока будет так
            LOGGER.log(Level.CONFIG, "Файл настроек не найден");
        }
        if (!thereAreSettings) {
            settServerSettings();
        } else {
            System.out.println("Установить новые настройки сервера y/n?");
            String sw = scanner.nextLine();
            if ("y".equals(sw)) {
                settServerSettings();
                LOGGER.log(Level.CONFIG, "Установлены настройки сервера");
            }
            System.out.println("Настройки без изменений");
            LOGGER.log(Level.CONFIG, "Сервер будет запущен с предыдущими настройками");
        }
    }

    public static void settServerSettings(){
        System.out.println("Для запуска сервера введите хост");//хотя по идее всегда будет хост запущенной машины, но пусть будет
        String h = "host=" + scanner.nextLine();
        System.out.println("Для запуска сервера введите порт");
        String p = "port=" + scanner.nextLine();
//        запишем настройки в файл настроек сервера и клиента ( файл настроек поместим выше всех, что бы он был доступным для сервера и клиента,
//        что бы сервак его перезаписывал, а клиент всегда мог обновить)
        try (FileWriter fileWriter = new FileWriter("src/main/resources/settings.properties", false)) {
            fileWriter.write(h);
            fileWriter.append('\n');
            fileWriter.write(p);
            fileWriter.append('\n');
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startServer() {
        //теперь чтение настроек и запуск сервера
        System.out.println("Запускаю сервер");
        Properties settings = new Properties();
        try {
            //выгрузим файл настроек
            settings.load(new FileInputStream("src/main/resources/settings.properties"));
            //прочитаем требуемые значения
            port = Integer.parseInt(settings.getProperty("port"));
            System.out.println("Порт для сервера: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //запускаем процесс выделения нового потока для клиента, ждем подключения, при подключении передаем сокет клиента в лист
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен" + getCurrentDate());
            LOGGER.log(Level.INFO, "Сервер запущен");
            //оборачиваем подключение клиента в вечный цикл, для возможности подключения любого кол-ва клиентов
            while (true) {
                Socket client = serverSocket.accept();
                try {
//                    clientList.add(new ServerForClientInMultiThread(client));
//                    clientList.getLast().start();
                    clientListCSLM.add(new ServerForClientInMultiThread(client));
                    clientListCSLM.last().start();
                    LOGGER.log(Level.INFO, "Зашел новый клиент");
                } catch (IOException e) {
                    client.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("сервер не запустился" + getCurrentDate());
            LOGGER.log(Level.WARNING, "Сервер не запустился");
        }
    }

    public static String getCurrentDate() {
        Date time;
        String dTime;

        time = new Date();//выставляем текущую дату
        dTime = DATE_FORMAT.format(time);
        return " { " + dTime + " } ";
    }
}