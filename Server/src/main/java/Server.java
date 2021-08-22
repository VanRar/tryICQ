import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Server {
    public static Scanner scanner = new Scanner(System.in);
    public static int port;
    protected static final Logger LOGGER = Logger.getLogger("ServerLogger");

    //список подключений с потоками ввода и вывода, по сути сервер для каждого клиента
    public static LinkedList<ServerForClientInMultiThread> clientList = new LinkedList<>();

    public static void main(String[] args) {


        //Установка порта для подключения клиентов через файл настроек (например, settings.txt);+
        //Возможность подключиться к серверу в любой момент и присоединиться к чату;+
        //Отправка новых сообщений клиентам;+
        //Запись всех отправленных через сервер сообщений с указанием имени пользователя и времени отправки.+

        setupLogger();
        //проверяем наличие настроек сервера и заполняем их
        installationSettingsProperties();
        //запускаем сервер и ждем подключения клиентов в многопоточном режиме
        //clientConnection(startServer());
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
        //по хорошему надо сначала показать существующие настройки
        boolean thereAreSettings = false;
        Properties settings = new Properties();
        try {
            //выгрузим файл настроек
            settings.load(new FileInputStream("src/main/resources/settings.properties"));
            //прочитаем требуемые значения
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
        //        Установка порта для подключения клиентов через файл настроек (например, settings.txt);
        System.out.println("Для запуска сервера введите хост");//хотя по идее всегда будет сервер запущенной машины, но пусть будет
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

        //запускаем сервер, ждем подключения, при подключении передаем сокет клиента в лист
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен" + getCurrentDate());
            LOGGER.log(Level.INFO, "Сервер запущен");
            //оборачиваем подключение клиента в вечный цикл, для возможности подключения любого кол-ва клиентов
            while (true) {
                Socket client = serverSocket.accept();
                try {
                    clientList.add(new ServerForClientInMultiThread(client));
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
        SimpleDateFormat dateFormat;

        time = new Date();//выставляем текущую дату
        dateFormat = new SimpleDateFormat("HH:mm:ss"); //устанавливаем формат даты
        dTime = dateFormat.format(time);
        return " { " + dTime + " } ";
    }
}
/* в одном потоке */
//      получается так не получается, так как сервак закрывается, если его реализовывать в другом блоке try

//        public static void clientConnection(ServerSocket serverSocket) {
//        while (true) {
//            if(serverSocket == null) {
//                System.out.println("Сервер не был запущен");
//                break;
//            }
//            try (//подключение клиента
//                 Socket client = serverSocket.accept()) {
//                //для записи клиенту
//                BufferedWriter writer =
//                        new BufferedWriter(
//                                new OutputStreamWriter(
//                                        client.getOutputStream()));
//                //для чтения клиента
//                BufferedReader reader =
//                        new BufferedReader(
//                                new InputStreamReader(
//                                        client.getInputStream()));
//                //пока без потоков:
//                System.out.println("Клиент подключился");
//                //посмотрим запрос клиента
//                String request = reader.readLine();
//                //вернем клиенту запрос
//                String response = "Сервер получил от вас" + request;
//                System.out.println(response);
//                writer.write(response);
//                writer.newLine();
//                writer.flush();

//идём писать клиента
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                break;
//            }
//        }
//    }
//}


/* не совсем удачная попытка многопоточном режиме */
//               //по сути сервак должен от всех пользователей получать сообщение вид имя: сообщение и всем его рассылать, кроме отправителя получается.
////                    System.out.println("Клиент подключился");
////                    //приветствуем нового участника
////                    writer.write("Вы вошли в чат");
////                    writer.newLine();
////                    writer.flush();
//
//                //посмотрим запрос клиента и отправим его всем, и по сути, наверное, это уже поток
//                new Thread(() -> {
//
//                    //для записи клиенту
//                    try (Socket client = serverSocket.accept();
//                         BufferedWriter writer =
//                                 new BufferedWriter(
//                                         new OutputStreamWriter(
//                                                 client.getOutputStream()));
//                         //для чтения клиента
//                         BufferedReader reader =
//                                 new BufferedReader(
//                                         new InputStreamReader(
//                                                 client.getInputStream()))) {
//                        writer.write("Enter the name thread");
//                        writer.newLine();
//                        writer.flush();
//                        System.out.println(Thread.currentThread().getName());
//                        String nameThread = reader.readLine();
//                        Thread.currentThread().setName(nameThread);
//                        System.out.println(Thread.currentThread().getName());
//
//
//                        //здесь делаем бесконечный цикл отправки и получения сообщений от клиента и по условию выходим.
//                        while (true) {
//                            String req = reader.readLine();
//                            //вернем клиенту запрос
//                            //надо здесь сделать рассылку всем клиентам
//                            //осталось узнать как понять кто подключен
//                            //response = request;
//                            System.out.println(response);
//                            if ("/exit".equals(req)) {
//                                System.out.println("пользователь??? вышел из чата");
//                                break;
//                            }
//                            if (!req.equals(request)) {
//                                //wait();
//                                //эта часть получается должна на сервере ждать сообщения от любого пользователя и отправлять его всем
//                                //но тогда получается сервак не статик
//                                writer.write(req);
//                                writer.newLine();
//                                writer.flush();
//                            }
//
//
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        System.out.println("полетел цикл отправки сообщений");
//                    }
//                }).start();